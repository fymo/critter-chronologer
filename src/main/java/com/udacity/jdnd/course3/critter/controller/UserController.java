package com.udacity.jdnd.course3.critter.controller;

import com.google.common.collect.Sets;
import com.udacity.jdnd.course3.critter.controller.dto.CustomerDTO;
import com.udacity.jdnd.course3.critter.controller.dto.EmployeeDTO;
import com.udacity.jdnd.course3.critter.controller.dto.EmployeeRequestDTO;
import com.udacity.jdnd.course3.critter.controller.dto.EmployeeSkill;
import com.udacity.jdnd.course3.critter.model.entity.Customer;
import com.udacity.jdnd.course3.critter.model.entity.Employee;
import com.udacity.jdnd.course3.critter.service.CustomerService;
import com.udacity.jdnd.course3.critter.service.EmployeeService;
import com.udacity.jdnd.course3.critter.service.PetService;
import com.udacity.jdnd.course3.critter.service.ScheduleService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles web requests related to Users.
 *
 * Includes requests for both customers and employees. Splitting this into separate user and customer controllers
 * would be fine too, though that is not part of the required scope for this class.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PetService petService;


    @PostMapping("/customer")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){

        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);

        Customer savedCustomer = customerService.save(customer);

        BeanUtils.copyProperties(savedCustomer, customerDTO);

        return customerDTO;
    }

    @GetMapping("/customer")
    public List<CustomerDTO> getAllCustomers(){

        return customerService.findAll().stream().map(customer -> {
            CustomerDTO customerDTO = new CustomerDTO();
            BeanUtils.copyProperties(customer, customerDTO);
            if(customer.getPets() != null)
                customerDTO.setPetIds(customer.getPets().stream().map(pet -> { return pet.getId(); }).collect(Collectors.toList()));

            return customerDTO;
        }).collect(Collectors.toList());
    }

    @GetMapping("/customer/pet/{petId}")
    public CustomerDTO getOwnerByPet(@PathVariable long petId){

        Customer customer = petService.findById(petId).getOwner();
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer, customerDTO);
        if(customer.getPets() != null)
            customerDTO.setPetIds(customer.getPets().stream().map(pet -> { return pet.getId(); }).collect(Collectors.toList()));

        return customerDTO;
    }

    @PostMapping("/employee")
    public EmployeeDTO saveEmployee(@RequestBody EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);

        BeanUtils.copyProperties(employeeService.save(employee), employeeDTO);

        return employeeDTO;
    }

    @PostMapping("/employee/{employeeId}")
    public EmployeeDTO getEmployee(@PathVariable long employeeId) {
        Employee employee = employeeService.findById(employeeId);

        EmployeeDTO employeeDTO = new EmployeeDTO();

        BeanUtils.copyProperties(employee, employeeDTO);

        return employeeDTO;
    }

    @PutMapping("/employee/{employeeId}")
    public void setAvailability(@RequestBody Set<DayOfWeek> daysAvailable, @PathVariable long employeeId) {
        Employee employee = employeeService.findById(employeeId);
        employee.setDaysAvailable(daysAvailable);
        employeeService.save(employee);
    }

    @GetMapping("/employee/availability")
    public List<EmployeeDTO> findEmployeesForService(@RequestBody EmployeeRequestDTO employeeDTO) {

        List<Employee> employees = employeeService.findAvailableEmployees(employeeDTO.getSkills(), employeeDTO.getDate().getDayOfWeek());

        return employees.stream().map(employee -> {
            EmployeeDTO dto = new EmployeeDTO();
            BeanUtils.copyProperties(employee, dto);
            return dto;
        }).collect(Collectors.toList());
    }

}
