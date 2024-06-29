package com.smarttech.SmartRH.AppResouces.Services;

import com.smarttech.SmartRH.AppResouces.Exceptions.UserException;
import com.smarttech.SmartRH.AppResouces.Models.DTOs.SoldeDto;
import com.smarttech.SmartRH.AppResouces.Models.Entities.Solde;
import com.smarttech.SmartRH.AppResouces.Models.Entities.User;
import com.smarttech.SmartRH.AppResouces.Repository.SoldeRepository;
import com.smarttech.SmartRH.AppResouces.Repository.UserRepository;
import com.smarttech.SmartRH.AppResouces.Services.interfaces.SoldeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SoldeServiceImpl implements SoldeService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SoldeRepository soldeRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public SoldeDto addSolde(Long userId) throws UserException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("user does not exist"));
        Solde solde = new Solde();
        solde.setUser(user);
        solde.setTotalCongeJours(30);
        solde.setPrisCongeJours(0);
        solde.setTotalSortieMin(240);
        solde.setPrisSortieMin(0);
        solde.setCompteurSortie(0);
        Solde s = soldeRepository.save(solde);
        SoldeDto soldeDto = modelMapper.map(s, SoldeDto.class);
        soldeDto.setUserId(s.getUser().getId());

        return soldeDto;
    }

    @Override
    public SoldeDto updateSolde(Long userId, SoldeDto dto) throws UserException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("user does not exist"));
        Solde solde = soldeRepository.findByUser(user).orElseThrow(() -> new UserException("user does not have solde"));

        if (dto.getTotalSortieMin() != solde.getPrisSortieMin()){solde.setTotalSortieMin(dto.getTotalSortieMin());}
        if (dto.getTotalCongeJours() != solde.getTotalCongeJours()){solde.setTotalCongeJours(dto.getTotalCongeJours());}
        if (dto.getPrisSortieMin() != solde.getPrisSortieMin()){solde.setPrisSortieMin(dto.getPrisSortieMin());}
        if (dto.getPrisCongeJours() != solde.getPrisCongeJours()){solde.setPrisCongeJours(dto.getPrisCongeJours());}
        if (dto.getCompteurSortie() != solde.getCompteurSortie()){solde.setCompteurSortie(dto.getCompteurSortie());}

        Solde newSolde = soldeRepository.save(solde);

        SoldeDto soldeDto = modelMapper.map(newSolde, SoldeDto.class);
        soldeDto.setUserId(solde.getUser().getId());

        return soldeDto;
    }

    @Override
    public SoldeDto deleteSolde(Long userId) throws UserException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("user does not exist"));
        Solde solde = soldeRepository.findByUser(user).orElseThrow(() -> new UserException("user does not have solde"));
        SoldeDto soldeDto = modelMapper.map(solde, SoldeDto.class);
        soldeDto.setUserId(solde.getUser().getId());

        soldeRepository.delete(solde);
        return soldeDto;
    }

    @Override
    public SoldeDto getSolde(Long userId) throws UserException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("user does not exist"));
        Solde solde = soldeRepository.findByUser(user).orElseThrow(() -> new UserException("user does not have solde"));

        SoldeDto soldeDto = modelMapper.map(solde, SoldeDto.class);
        soldeDto.setUserId(solde.getUser().getId());

        return soldeDto;
    }

    @Override
    public List<SoldeDto> getAllSolde() {
        List<Solde> soldeList = soldeRepository.findAll();
        List<SoldeDto> lista = new ArrayList<SoldeDto>();
        for (Solde s : soldeList){
            SoldeDto soldeDto = modelMapper.map(s, SoldeDto.class);
            soldeDto.setUserId(s.getUser().getId());
            lista.add(soldeDto);
        }
        return lista;
    }

    @Override
    public SoldeDto subtractFromSoldeConge(Long userId, int prisCongeJours) throws UserException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("user does not exist"));
        Solde solde = soldeRepository.findByUser(user).orElseThrow(() -> new UserException("user does not have solde"));

        solde.setPrisCongeJours(prisCongeJours + solde.getPrisCongeJours());
        Solde newSolde = soldeRepository.save(solde);
        SoldeDto soldeDto = modelMapper.map(newSolde, SoldeDto.class);
        soldeDto.setUserId(newSolde.getUser().getId());

        return soldeDto;
    }

    @Override
    public SoldeDto subtractFromSoldeSortie(Long userId, int prisSortieMin) throws UserException {

        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("user does not exist"));
        Solde solde = soldeRepository.findByUser(user).orElseThrow(() -> new UserException("user does not have solde"));

        solde.setPrisSortieMin(prisSortieMin + solde.getPrisSortieMin());
        solde.setCompteurSortie(solde.getCompteurSortie() + 1);
        Solde newSolde = soldeRepository.save(solde);
        SoldeDto soldeDto = modelMapper.map(newSolde, SoldeDto.class);
        soldeDto.setUserId(newSolde.getUser().getId());

        return soldeDto;
    }

    @Override
    public SoldeDto addToSoldeConge(Long userId, int prisCongeJours) throws UserException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("user does not exist"));
        Solde solde = soldeRepository.findByUser(user).orElseThrow(() -> new UserException("user does not have solde"));

        solde.setPrisCongeJours(solde.getPrisCongeJours() - prisCongeJours);
        Solde newSolde = soldeRepository.save(solde);
        SoldeDto soldeDto = modelMapper.map(newSolde, SoldeDto.class);
        soldeDto.setUserId(newSolde.getUser().getId());

        return soldeDto;
    }

    @Override
    public SoldeDto addToSoldeSortie(Long userId, int prisSortieMin) throws UserException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("user does not exist"));
        Solde solde = soldeRepository.findByUser(user).orElseThrow(() -> new UserException("user does not have solde"));

        solde.setPrisSortieMin(solde.getPrisSortieMin() - prisSortieMin);
        solde.setCompteurSortie(solde.getCompteurSortie() + 1);
        Solde newSolde = soldeRepository.save(solde);
        SoldeDto soldeDto = modelMapper.map(newSolde, SoldeDto.class);
        soldeDto.setUserId(newSolde.getUser().getId());

        return soldeDto;
    }
}
