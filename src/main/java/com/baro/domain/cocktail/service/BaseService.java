package com.baro.domain.cocktail.service;

import com.baro.domain.cocktail.domain.Base;
import com.baro.domain.cocktail.repository.DAO.BaseDAO;
import com.baro.domain.cocktail.repository.DAO.LIstBaseDAO;
import com.baro.domain.cocktail.repository.DTO.BaseUploadDTO;
import com.baro.domain.cocktail.repository.JPABaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BaseService {
    private final JPABaseRepository baseRepository;

    /**
     * 다른곳에도 사용되는 애들
     */
    public boolean checkBase(String base_en_name){
        return baseRepository.existsByName(base_en_name);
    }
    public boolean checkBaseToSeq(Long baseSeq){
        return baseRepository.existsById(baseSeq);
    }

    public Base findBase(String base_en_name){
       return baseRepository.findByName(base_en_name);
    }

    public Base findBaseToSeq(Long baseSeq){
        return baseRepository.findBySeq(baseSeq);
    }

    /**
     * 다른곳에도 사용되는 애들
     */

    public BaseDAO base_object_service(Long seq){
        if(baseRepository.existsById(seq)){
            //존재
            Base baseData = baseRepository.findBySeq(seq);
            BaseDAO baseDAO = convertToBase(baseData);
            return baseDAO;
        }else{
            //존재하지않음
            log.info("존재하지않은 베이스");
            return null;
        }

    }

    private BaseDAO convertToBase(Base base){
        BaseDAO baseDAO = new BaseDAO();
        baseDAO.setKR_Name(base.getKrName());
        baseDAO.setEN_Name(base.getName());
        baseDAO.setPrice(base.getPrice());
        baseDAO.setAmount(base.getAmount());
        baseDAO.setAlcohol(base.getAlcohol());
        baseDAO.setContent(base.getContentL());
        baseDAO.setImgURL(base.getFileURL());

        return baseDAO;
    }

    public List<LIstBaseDAO> base_list_service(){
        List<Base> baseList = baseRepository.findAll();

        List<LIstBaseDAO> listBaseDAOList = baseList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return listBaseDAOList;
    }

    private LIstBaseDAO convertToDTO(Base base) {
        LIstBaseDAO listBaseDAO = new LIstBaseDAO();
        listBaseDAO.setSeq(base.getSeq());
        listBaseDAO.setEN_Name(base.getName());
        listBaseDAO.setKR_Name(base.getKrName());
        listBaseDAO.setFileURL(base.getFileURL());


        return listBaseDAO;
    }

    public String base_upload_service(String imgURL , BaseUploadDTO baseUploadDTO){
        log.info("base upload service start");
        String return_text;
        try{
            baseRepository.save(
                    Base.builder()
                            .name(baseUploadDTO.getEN_Name())
                            .krName(baseUploadDTO.getKR_Name())
                            .price(baseUploadDTO.getPrice())
                            .amount(baseUploadDTO.getAmount())
                            .alcohol(baseUploadDTO.getAlcohol())
                            .fileURL(imgURL)
                            .contentL(baseUploadDTO.getContent())
                            .build()
            );
            log.info("베이스 업로드 완료...");
            return_text ="success";

        }catch (DataIntegrityViolationException e) {
            // 데이터베이스 무결성 제약 조건 위반 - 키 중복  or 조건 위배
            return_text = "사용자의 데이터 제대로 검증되지 않았습니다.";
            log.warn("admin_register_service : {}" , return_text);
        } catch (JpaSystemException e) {
            // JPA 연동 중 문제 발생
            return_text = "데이터베이스 연동 중 오류가 발생";
            log.warn("admin_register_service : {}" , return_text);
        } catch (DataAccessException e) {
            // 데이터 액세스 오류
            return_text = "데이터베이스 액세스 중 오류가 발생";
            log.warn("admin_register_service : {}" , return_text);
        } catch (Exception e) {
            // 다른 모든 예외 처리
            return_text = "알 수 없는 오류가 발생";
            log.warn(e.getMessage());
        }

        return return_text;
    }
}
