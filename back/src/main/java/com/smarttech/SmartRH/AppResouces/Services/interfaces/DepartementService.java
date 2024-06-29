package com.smarttech.SmartRH.AppResouces.Services.interfaces;

import com.smarttech.SmartRH.AppResouces.Exceptions.DemandeException;
import com.smarttech.SmartRH.AppResouces.Exceptions.DepartementException;
import com.smarttech.SmartRH.AppResouces.Exceptions.PermissionException;
import com.smarttech.SmartRH.AppResouces.Exceptions.UserException;
import com.smarttech.SmartRH.AppResouces.Models.DTOs.DepartementDto;

import java.io.IOException;
import java.util.List;

public interface DepartementService {
    public DepartementDto addDepartement(DepartementDto dto) throws DepartementException, PermissionException;

    public DepartementDto updateDepartement(Long deptId, DepartementDto dto) throws DepartementException, PermissionException, UserException;

    public DepartementDto deleteDepartement(Long deptId) throws DepartementException, PermissionException, UserException, IOException, DemandeException;

    public DepartementDto getDepartementById(Long deptId) throws DepartementException;

    public List<DepartementDto> getAllDepartements() throws DepartementException;
}
