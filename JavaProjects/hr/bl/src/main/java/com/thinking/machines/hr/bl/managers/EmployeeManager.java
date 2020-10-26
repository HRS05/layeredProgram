package com.thinking.machines.hr.bl.managers; 
import com.thinking.machines.hr.bl.interfaces.managers.*;
import com.thinking.machines.hr.bl.interfaces.pojo.*;
import com.thinking.machines.hr.bl.pojo.*;
import com.thinking.machines.hr.bl.exceptions.*;
import com.thinking.machines.enums.*;
import com.thinking.machines.hr.dl.exceptions.*;
import com.thinking.machines.hr.dl.interfaces.dto.*;
import com.thinking.machines.hr.dl.interfaces.dao.*;
import com.thinking.machines.hr.dl.dto.*;
import com.thinking.machines.hr.dl.dao.*;
import java.util.*;
import java.math.*;
import java.text.*;
public class EmployeeManager implements EmployeeManagerInterface
{

private Map<String,EmployeeInterface> employeeIdWiseEmployeesMap;
private Map<String,EmployeeInterface> panNumberWiseEmployeesMap;
private Map<String,EmployeeInterface> aadharCardNumberWiseEmployeesMap;
private Set<EmployeeInterface> employeesSet;
private static EmployeeManager  employeeManager=null;
private EmployeeManager() throws BLException
{
populateDataStructure(); 
}//constructor ends

private void populateDataStructure() throws BLException
{
this.employeeIdWiseEmployeesMap=new HashMap<>();
this.panNumberWiseEmployeesMap=new HashMap<>();
this.aadharCardNumberWiseEmployeesMap=new HashMap<>();
this.employeesSet=new TreeSet<>();
try
{
Set<EmployeeDTOInterface> dlEmployees;
dlEmployees=new EmployeeDAO().getAll();
EmployeeInterface employee;

DesignationManagerInterface designationManager;
designationManager=DesignationManager.getDesignationManager();
DesignationInterface designation;
for(EmployeeDTOInterface dlEmployee:dlEmployees)
{
employee=new Employee();
employee.setEmployeeId(dlEmployee.getEmployeeId());
employee.setName(dlEmployee.getName());
designation=designationManager.getDesignationByCode(dlEmployee.getDesignationCode());
employee.setDesignation(designation);
employee.setDateOfBirth(dlEmployee.getDateOfBirth());
if(dlEmployee.getGender()=='M') employee.setGender(GENDER.MALE);
else employee.setGender(GENDER.FEMALE);
employee.setIsIndian(dlEmployee.getIsIndian());
employee.setBasicSalary(dlEmployee.getBasicSalary());
employee.setPANNumber(dlEmployee.getPANNumber());
employee.setAadharCardNumber(dlEmployee.getAadharCardNumber());

this.employeeIdWiseEmployeesMap.put(employee.getEmployeeId().toUpperCase(),employee);
this.panNumberWiseEmployeesMap.put(employee.getPANNumber().toUpperCase(),employee);
this.aadharCardNumberWiseEmployeesMap.put(employee.getAadharCardNumber().toUpperCase(),employee);
this.employeesSet.add(employee);
}
}catch(DAOException daoException)
{
BLException blException=new BLException();
blException.setGenericException(daoException.getMessage());
throw blException;
}
}//function ends


public static EmployeeManagerInterface getEmployeeManager() throws BLException
{
if(employeeManager==null) employeeManager=new EmployeeManager();
return employeeManager;
}//function ends

public void addEmployee(EmployeeInterface employee) throws BLException
{
BLException blException=new BLException();
String employeeId=employee.getEmployeeId();
String name=employee.getName();
DesignationInterface designation=employee.getDesignation();
int designationCode=0;
Date dateOfBirth=employee.getDateOfBirth();
char gender=employee.getGender();
boolean isIndian=employee.getIsIndian();
BigDecimal basicSalary=employee.getBasicSalary();
String panNumber=employee.getPANNumber();
String aadharCardNumber=employee.getAadharCardNumber();
if(employeeId!=null)
{
employeeId=employeeId.trim();
if(employeeId.length()>0)
{
blException.addException("employeeId","Employee Id. should be nil/emplty");
}
}

if(name==null) 
{
blException.addException("name","Name required");
}
else 
{
name=name.trim();
if(name.length()==0) blException.addException("name","Name required");
}

DesignationManagerInterface designationManager;
designationManager=DesignationManager.getDesignationManager();
if(designation==null)
{
blException.addException("designation","Designation required");
}
else
{
designationCode=designation.getCode();
if(designationManager.designationCodeExists(designationCode)==false)
{
blException.addException("designation","Invalid designation");
}
}

if(dateOfBirth==null)
{
blException.addException("dateOfBirth","Date of birth required");
}

if(gender==' ')
{
blException.addException("gender","Gender required");
}

if(basicSalary==null)
{
blException.addException("basicSalary","Basic salary required");
}
else
{
if(basicSalary.signum()==-1)
{
blException.addException("basicSalary","Basic salary cannot be negative");
}
}

if(panNumber==null)
{
blException.addException("panNumber","PAN Number required");
}
else
{
panNumber=panNumber.trim();
if(panNumber.length()==0)
{
blException.addException("panNumber","PAN Number required");
}
}

if(aadharCardNumber==null)
{
blException.addException("aadharCardNumber","Aadhar-Card Number required");
}
else
{
aadharCardNumber=aadharCardNumber.trim();
if(aadharCardNumber.length()==0)
{
blException.addException("aadharCardNumber","Aadhar-Card Number required");
}
}
if(panNumber!=null && panNumber.length()>0)
{
if(this.panNumberWiseEmployeesMap.containsKey(panNumber.toUpperCase()))
{
blException.addException("panNumber","PAN Number "+panNumber+" exists");
}
}
if(aadharCardNumber!=null && aadharCardNumber.length()>0)
{
if(this.aadharCardNumberWiseEmployeesMap.containsKey(aadharCardNumber.toUpperCase()))
{
blException.addException("aadharCardNumber","Aadhar-Card Number "+aadharCardNumber+" exists");
}
}
if(blException.hasExceptions())
{
throw blException;
}

try
{
EmployeeDAOInterface employeeDAO;
employeeDAO=new EmployeeDAO();
EmployeeDTOInterface dlEmployee;
dlEmployee=new EmployeeDTO();
dlEmployee.setName(name);
dlEmployee.setDesignationCode(designation.getCode());
dlEmployee.setDateOfBirth(dateOfBirth);
dlEmployee.setGender((gender=='M')?GENDER.MALE:GENDER.FEMALE);
dlEmployee.setBasicSalary(basicSalary);
dlEmployee.setIsIndian(isIndian);
dlEmployee.setPANNumber(panNumber);
dlEmployee.setAadharCardNumber(aadharCardNumber);
employeeDAO.add(dlEmployee);
employee.setEmployeeId(dlEmployee.getEmployeeId());
//update dataStructure
EmployeeInterface dsEmployee=new Employee();
dsEmployee.setEmployeeId(employee.getEmployeeId());
dsEmployee.setName(name);
dsEmployee.setDesignation(((DesignationManager)designationManager).getDSDesignationByCode(designation.getCode()));
dsEmployee.setGender((gender=='M')?GENDER.MALE:GENDER.FEMALE);
dsEmployee.setBasicSalary(basicSalary);
dsEmployee.setIsIndian(isIndian);
dsEmployee.setPANNumber(panNumber);
dsEmployee.setAadharCardNumber(aadharCardNumber);

this.employeesSet.add(dsEmployee);
this.employeeIdWiseEmployeesMap.put(dsEmployee.getEmployeeId().toUpperCase(),dsEmployee);
this.panNumberWiseEmployeesMap.put(panNumber.toUpperCase(),dsEmployee);
this.aadharCardNumberWiseEmployeesMap.put(aadharCardNumber.toUpperCase(),dsEmployee);

}catch(DAOException daoException)
{
blException.setGenericException("daoException.getMessage()");
throw blException;
}




}//function ends


public void updateEmployee(EmployeeInterface employee) throws BLException
{
BLException blException=new BLException();
blException.setGenericException("Not yet implemented");
throw blException;
}
public void removeEmployee(String employeeId) throws BLException
{
BLException blException=new BLException();
blException.setGenericException("Not yet implemented");
throw blException;
}
public EmployeeInterface getEmployeeByEmployeeId(String employeeId) throws BLException
{
BLException blException=new BLException();
blException.setGenericException("Not yet implemented");
throw blException;
}
public EmployeeInterface getEmployeeByPANNumber(String panNumber) throws BLException
{
BLException blException=new BLException();
blException.setGenericException("Not yet implemented");
throw blException;
}
public EmployeeInterface getEmployeeByAadharCardNumber(String aadharCardNumber) throws BLException
{
BLException blException=new BLException();
blException.setGenericException("Not yet implemented");
throw blException;
}
public int getEmployeeCount() throws BLException
{
BLException blException=new BLException();
blException.setGenericException("Not yet implemented");
throw blException;
}
public boolean employeeIdExists(String employeeId) throws BLException
{
BLException blException=new BLException();
blException.setGenericException("Not yet implemented");
throw blException;
}
public boolean employeePANNumberExists(String panNumber) throws BLException
{
BLException blException=new BLException();
blException.setGenericException("Not yet implemented");
throw blException;
}
public boolean employeeAadharCardNumberExists(String aadharCardNumber) throws BLException
{
BLException blException=new BLException();
blException.setGenericException("Not yet implemented");
throw blException;
}
public Set<EmployeeInterface> getEmployee() throws BLException
{
BLException blException=new BLException();
blException.setGenericException("Not yet implemented");
throw blException;
}
public Set<EmployeeInterface> getEmployeeByDesignationCode(int designationCode) throws BLException
{
BLException blException=new BLException();
blException.setGenericException("Not yet implemented");
throw blException;
}
public int getEmployeeCountByDesignationCode(int designationCode) throws BLException
{
BLException blException=new BLException();
blException.setGenericException("Not yet implemented");
throw blException;
}
public boolean designationAlloted(int designationCode) throws BLException
{
BLException blException=new BLException();
blException.setGenericException("Not yet implemented");
throw blException;
}

}//class ends 