package com.smarttech.SmartRH.AppResouces.Services;

import com.smarttech.SmartRH.AppResouces.Exceptions.DemandeException;
import com.smarttech.SmartRH.AppResouces.Exceptions.DepartementException;
import com.smarttech.SmartRH.AppResouces.Exceptions.PermissionException;
import com.smarttech.SmartRH.AppResouces.Exceptions.UserException;
import com.smarttech.SmartRH.AppResouces.Models.DTOs.DepartementDto;
import com.smarttech.SmartRH.AppResouces.Models.ENUMs.Role;
import com.smarttech.SmartRH.AppResouces.Models.ENUMs.TypeDepartement;
import com.smarttech.SmartRH.AppResouces.Models.Entities.Departement;
import com.smarttech.SmartRH.AppResouces.Models.Entities.User;
import com.smarttech.SmartRH.AppResouces.Repository.DepartementRepository;
import com.smarttech.SmartRH.AppResouces.Repository.UserRepository;
import com.smarttech.SmartRH.AppResouces.Services.interfaces.DepartementService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Service
public class DepartementServiceImpl implements DepartementService {
    @Autowired
    private DepartementRepository departementRepository;
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public DepartementDto addDepartement(DepartementDto dto) throws DepartementException, PermissionException {
        User currentUser = userService.getCurrentUser();
        if((currentUser.getRole() != Role.DG) && (currentUser.getRole() != Role.RRH)) throw new PermissionException("You don't have permission");

        List<Departement> list = departementRepository.findAll()
                .stream().filter((dept) -> dept.getName().toLowerCase().equals(dto.getName().toLowerCase()))
                .collect(Collectors.toList());

        List<Departement> listRH = departementRepository.findAll()
                .stream().filter((dept) -> dept.getType() == TypeDepartement.RH)
                .collect(Collectors.toList());

        List<Departement> listDirection = departementRepository.findAll()
                .stream().filter((dept) -> dept.getType() == TypeDepartement.DIRECTION)
                .collect(Collectors.toList());



        if(list.size() > 0) throw new DepartementException("Department already exists with this name");
        if((listRH.size() > 0) && (dto.getType() == TypeDepartement.RH)) throw new DepartementException("Department RH already exists");
        if((listDirection.size() > 0) && (dto.getType() == TypeDepartement.DIRECTION)) throw new DepartementException("Department DIRECTION already exists");
        Departement newDepartement = new Departement();
        newDepartement.setName(dto.getName());
        newDepartement.setType(dto.getType());
        Departement depObj = departementRepository.save(newDepartement);

        DepartementDto depDto = modelMapper.map(depObj, DepartementDto.class);

        return depDto;
    }

    @Override
    public DepartementDto updateDepartement(Long deptId, DepartementDto dto) throws DepartementException, PermissionException, UserException {
        User currentUser = userService.getCurrentUser();
        if((currentUser.getRole() != Role.DG) && (currentUser.getRole() != Role.RRH)) throw new PermissionException("You don't have permission");
        Departement dep = departementRepository.findById(deptId)
                .orElseThrow(() -> new DepartementException("No department exists with this id"));

        if(dep.getName() != dto.getName()){
            dep.setName(dto.getName());
        }
        if((Objects.isNull(dep.getChefDepartement()) && Objects.nonNull(dto.getChefId())) || (Objects.nonNull(dep.getChefDepartement()) && Objects.nonNull(dto.getChefId()))){
        //if(dep.getChefDepartement().getId() != dto.getChefId()){
            List<User> members = dep.getUsers();
            if(members.size() == 0) throw new DepartementException("You must add users to this department first");
            if(Objects.nonNull(dto.getChefId()) && Objects.nonNull(dep.getChefDepartement())){
                User oldChef = userRepository.findById(dep.getChefDepartement().getId())
                        .orElseThrow(() -> new UserException("No User Found"));
                User newChef = userRepository.findById(dto.getChefId())
                        .orElseThrow(() -> new UserException("No User Found"));

                if(newChef.getDepartement().getId() != dep.getId()) throw new DepartementException("New Chef must be in the same Department");
                if(dep.getType() == TypeDepartement.DIRECTION){
                    if(currentUser.getRole() == Role.DG){
                        oldChef.setRole(Role.USER);
                        newChef.setRole(Role.DG);
                        User oldObj = userRepository.save(oldChef);
                        User newObj = userRepository.save(newChef);
                        dep.setChefDepartement(newObj);
                    }else{
                        throw new PermissionException("You don't have permission");
                    }
                } else if (dep.getType() == TypeDepartement.RH) {
                    oldChef.setRole(Role.USER);
                    newChef.setRole(Role.RRH);
                    User oldObj = userRepository.save(oldChef);
                    User newObj = userRepository.save(newChef);
                    dep.setChefDepartement(newObj);
                }else {
                    oldChef.setRole(Role.USER);
                    newChef.setRole(Role.CD);
                    User oldObj = userRepository.save(oldChef);
                    User newObj = userRepository.save(newChef);
                    dep.setChefDepartement(newObj);
                }
            }
            if(Objects.nonNull(dto.getChefId()) && Objects.isNull(dep.getChefDepartement())){
                User newChef = userRepository.findById(dto.getChefId())
                        .orElseThrow(() -> new UserException("No User Found"));
                if(newChef.getDepartement().getId() != dep.getId()) throw new DepartementException("New Chef must be in the same Department");
                if(dep.getType() == TypeDepartement.DIRECTION){
                    if(currentUser.getRole() == Role.DG){
                        newChef.setRole(Role.DG);
                        User newObj = userRepository.save(newChef);
                        dep.setChefDepartement(newObj);
                    }else{
                        throw new PermissionException("You don't have permission");
                    }
                } else if (dep.getType() == TypeDepartement.RH) {
                    newChef.setRole(Role.RRH);
                    User newObj = userRepository.save(newChef);
                    dep.setChefDepartement(newObj);
                }else {
                    newChef.setRole(Role.CD);
                    User newObj = userRepository.save(newChef);
                    dep.setChefDepartement(newObj);
                }
            }
        }
        Departement updatedDepartment = departementRepository.save(dep);
        DepartementDto depDto = modelMapper.map(updatedDepartment, DepartementDto.class);
        if (Objects.nonNull(updatedDepartment.getChefDepartement())){
            depDto.setChefId(updatedDepartment.getChefDepartement().getId());
        }

        return depDto;
    }

    @Override
    public DepartementDto deleteDepartement(Long deptId) throws DepartementException, PermissionException, UserException, IOException, DemandeException {
        User currentUser = userService.getCurrentUser();
        Departement dep = departementRepository.findById(deptId)
                .orElseThrow(() -> new DepartementException("No department exists with this id"));

        if((currentUser.getRole() != Role.DG) && (currentUser.getRole() != Role.RRH)) throw new PermissionException("You don't have permission");


        if(dep.getType() == TypeDepartement.DIRECTION || dep.getType() == TypeDepartement.RH){
            throw new DepartementException("This Department can't be Deleted");
        }else{
            List<User> users = dep.getUsers();
            if (!users.isEmpty()){
                for (User user: users) {
                    userService.deleteUser(user.getId());
                }
            }
            departementRepository.delete(dep);
            DepartementDto depDto = modelMapper.map(dep, DepartementDto.class);

            return depDto;
        }
    }

    @Override
    public DepartementDto getDepartementById(Long deptId) throws DepartementException {
        Departement department = departementRepository.findById(deptId)
                .orElseThrow(() -> new DepartementException("No department exist with this department id"));
        DepartementDto depDto = new DepartementDto();
        if(Objects.isNull(department.getChefDepartement())){
            depDto.setId(department.getId());
            depDto.setName(department.getName());
            depDto.setType(department.getType());
        }else{
            depDto = modelMapper.map(department,DepartementDto.class);
            depDto.setChefId(department.getChefDepartement().getId());
        }

        return depDto;
    }

    @Override
    public List<DepartementDto> getAllDepartements() throws DepartementException {
        List<Departement> departments = departementRepository.findAll();

        if(departments.isEmpty())
            throw new DepartementException("No department exists in DB");
        else {

            List<DepartementDto> list = new ArrayList<>();
            for(Departement department : departments) {
                DepartementDto depDto = new DepartementDto();
                if(Objects.isNull(department.getChefDepartement())){
                    depDto.setId(department.getId());
                    depDto.setName(department.getName());
                    depDto.setType(department.getType());
                }else{
                    depDto = modelMapper.map(department,DepartementDto.class);
                    depDto.setChefId(department.getChefDepartement().getId());
                }
                list.add(depDto);
            }
            return list;
        }
    }
}
