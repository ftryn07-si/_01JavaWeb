package com.juaracoding._01JavaWeb.service;

import com.juaracoding._01JavaWeb.configuration.OtherConfig;
import com.juaracoding._01JavaWeb.dto.MenuDTO;
import com.juaracoding._01JavaWeb.handler.ResourceNotFoundException;
import com.juaracoding._01JavaWeb.handler.ResponseHandler;
import com.juaracoding._01JavaWeb.model.Menu;
import com.juaracoding._01JavaWeb.repo.MenuRepo;
import com.juaracoding._01JavaWeb.utils.ConstantMessage;
import com.juaracoding._01JavaWeb.utils.LoggingFile;
import com.juaracoding._01JavaWeb.utils.TransformToDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
    KODE MODUL 03
 */
@Service
@Transactional
public class MenuService {

    private MenuRepo menuRepo;

    private String[] strExceptionArr = new String[2];
    @Autowired
    private ModelMapper modelMapper;

    private Map<String,Object> objectMapper = new HashMap<String,Object>();

    private TransformToDTO transformToDTO = new TransformToDTO();

    private Map<String,String> mapColumnSearch = new HashMap<String,String>();
    private Map<Integer, Integer> mapItemPerPage = new HashMap<Integer, Integer>();
    private String [] strColumnSearch = new String[2];


    @Autowired
    public MenuService(MenuRepo menuRepo) {
        mapColumn();
//        listItemPerPage();
        strExceptionArr[0] = "MenuService";
        this.menuRepo = menuRepo;
    }

    public Map<String, Object> saveMenu(Menu menu, WebRequest request) {
        String strMessage = ConstantMessage.SUCCESS_SAVE;
        Object strUserIdz = request.getAttribute("USR_IDZ",1);

        try {
            if(strUserIdz==null)
            {
                return new ResponseHandler().generateModelAttribut(ConstantMessage.ERROR_FLOW_INVALID,
                        HttpStatus.NOT_ACCEPTABLE,null,"FV03001",request);
            }
            menu.setCreatedBy(Integer.parseInt(strUserIdz.toString()));
            menu.setCreatedDate(new Date());
            menuRepo.save(menu);
        } catch (Exception e) {
            strExceptionArr[1] = "saveMenu(Menu menu, WebRequest request) --- LINE 67";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLogging());
            return new ResponseHandler().generateModelAttribut(ConstantMessage.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST, null, "FE03001", request);
        }
        return new ResponseHandler().generateModelAttribut(strMessage,
                HttpStatus.CREATED, null, null, request);
    }

    public Map<String, Object> updateMenu(Long idMenu,Menu menu, WebRequest request) {
        String strMessage = ConstantMessage.SUCCESS_SAVE;
        Object strUserIdz = request.getAttribute("USR_IDZ",1);

        try {
            Menu nextMenu = menuRepo.findById(idMenu).orElseThrow(
                    ()->null
            );

            if(nextMenu==null)
            {
                return new ResponseHandler().generateModelAttribut(ConstantMessage.WARNING_MENU_NOT_EXISTS,
                        HttpStatus.NOT_ACCEPTABLE,null,"FV03002",request);
            }
            if(strUserIdz==null)
            {
                return new ResponseHandler().generateModelAttribut(ConstantMessage.ERROR_FLOW_INVALID,
                        HttpStatus.NOT_ACCEPTABLE,null,"FV03003",request);
            }
            nextMenu.setNamaMenu(menu.getNamaMenu());
            nextMenu.setPathMenu(menu.getPathMenu());
            nextMenu.setMenuHeader(menu.getMenuHeader());
            nextMenu.setEndPoint(menu.getEndPoint());
            nextMenu.setModifiedBy(Integer.parseInt(strUserIdz.toString()));
            nextMenu.setModifiedDate(new Date());

        } catch (Exception e) {
            strExceptionArr[1] = "updateMenu(Long idMenu,Menu menu, WebRequest request) --- LINE 92";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLogging());
            return new ResponseHandler().generateModelAttribut(ConstantMessage.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST, null, "FE03002", request);
        }
        return new ResponseHandler().generateModelAttribut(strMessage,
                HttpStatus.CREATED, null, null, request);
    }



    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> saveUploadFileMenu(List<Menu> listMenu,
                                              MultipartFile multipartFile,
                                              WebRequest request) throws Exception {
        //url -> OtherConfig.
        List<Menu> listMenuResult = null;
        String strMessage = ConstantMessage.SUCCESS_SAVE;

        try {
            listMenuResult = menuRepo.saveAll(listMenu);
            if (listMenuResult.size() == 0) {
                strExceptionArr[1] = "saveUploadFile(List<Menu> listMenu, MultipartFile multipartFile, WebRequest request) --- LINE 82";
                LoggingFile.exceptionStringz(strExceptionArr, new ResourceNotFoundException("FILE KOSONG"), OtherConfig.getFlagLogging());
                return new ResponseHandler().generateModelAttribut(ConstantMessage.ERROR_EMPTY_FILE + " -- " + multipartFile.getOriginalFilename(),
                        HttpStatus.BAD_REQUEST, null, "FV03004", request);
            }
        } catch (Exception e) {
            strExceptionArr[1] = "saveUploadFile(List<Menu> listMenu, MultipartFile multipartFile, WebRequest request) --- LINE 88";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLogging());
            return new ResponseHandler().generateModelAttribut(ConstantMessage.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST, null, "FE03002", request);
        }
        return new ResponseHandler().
                generateModelAttribut(strMessage,
                HttpStatus.CREATED,
                null,
                null,
                        request);
    }

    public Map<String,Object> findAllMenu(Pageable pageable, WebRequest request)
    {
        List<MenuDTO> listMenuDTO = null;
        Map<String,Object> mapResult = null;
        Page<Menu> pageMenu = null;
        List<Menu> listMenu = null;

        try
        {
            pageMenu = menuRepo.findByIsDelete(pageable,(byte)1);
            listMenu = pageMenu.getContent();
            if(listMenu.size()==0)
            {
                return new ResponseHandler().
                        generateModelAttribut(ConstantMessage.WARNING_DATA_EMPTY,
                                HttpStatus.OK,
                                null,
                                null,
                                request);
            }
            listMenuDTO = modelMapper.map(listMenu, new TypeToken<List<MenuDTO>>() {}.getType());
            mapResult = transformToDTO.transformObject(objectMapper,listMenuDTO,pageMenu,mapColumnSearch);

        }
        catch (Exception e)
        {
            strExceptionArr[1] = "findAllMenu(Pageable pageable, WebRequest request) --- LINE 121";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLogging());
            return new ResponseHandler().generateModelAttribut(ConstantMessage.ERROR_INTERNAL_SERVER,
                    HttpStatus.INTERNAL_SERVER_ERROR, null, "FE03003", request);
        }

        return new ResponseHandler().
                generateModelAttribut(ConstantMessage.SUCCESS_FIND_BY,
                        HttpStatus.OK,
                        mapResult,
                        null,
                        null);
    }

    public Map<String,Object> findByPage(Pageable pageable,WebRequest request,String columFirst,String valueFirst)
    {
        Page<Menu> pageMenu = null;
        List<Menu> listMenu = null;
        List<MenuDTO> listMenuDTO = null;
        Map<String,Object> mapResult = null;

        try
        {
            if(columFirst.equals("id"))
            {
                try
                {
                    Long.parseLong(valueFirst);
                }
                catch (Exception e)
                {
                    strExceptionArr[1] = "findByPage(Pageable pageable,WebRequest request,String columFirst,String valueFirst) --- LINE 205";
                    LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLogging());
                    return new ResponseHandler().
                            generateModelAttribut(ConstantMessage.WARNING_DATA_EMPTY,
                                    HttpStatus.OK,
                                    transformToDTO.transformObjectDataEmpty(objectMapper,pageable,mapColumnSearch),//HANDLE NILAI PENCARIAN
                                    "FE03004",
                                    request);
                }
            }
            pageMenu = getDataByValue(pageable,columFirst,valueFirst);
            listMenu = pageMenu.getContent();
            if(listMenu.size()==0)
            {
                return new ResponseHandler().
                        generateModelAttribut(ConstantMessage.WARNING_DATA_EMPTY,
                                HttpStatus.OK,
                                null,
                                "FV03006",
                                request);
            }
            listMenuDTO = modelMapper.map(listMenu, new TypeToken<List<MenuDTO>>() {}.getType());
            mapResult = transformToDTO.transformObject(objectMapper,listMenuDTO,pageMenu,mapColumnSearch);
            System.out.println("LIST DATA => "+listMenuDTO.size());
        }

        catch (Exception e)
        {
            strExceptionArr[1] = "findByPage(Pageable pageable,WebRequest request,String columFirst,String valueFirst) --- LINE 232";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLogging());
            return new ResponseHandler().generateModelAttribut(ConstantMessage.ERROR_FLOW_INVALID,
                    HttpStatus.INTERNAL_SERVER_ERROR, null, "FE03005", request);
        }
        return new ResponseHandler().
                generateModelAttribut(ConstantMessage.SUCCESS_FIND_BY,
                        HttpStatus.OK,
                        mapResult,
                        null,
                        request);
    }

    public Map<String,Object> findById(Long id, WebRequest request)
    {
        Menu menu = menuRepo.findById(id).orElseThrow (
                ()-> null
        );
        if(menu == null)
        {
            return new ResponseHandler().generateModelAttribut(ConstantMessage.WARNING_MENU_NOT_EXISTS,
                    HttpStatus.NOT_ACCEPTABLE,null,"FV03005",request);
        }
        MenuDTO menuDTO = modelMapper.map(menu, new TypeToken<List<MenuDTO>>() {}.getType());
        return new ResponseHandler().
                generateModelAttribut(ConstantMessage.SUCCESS_FIND_BY,
                        HttpStatus.OK,
                        menuDTO,
                        null,
                        request);
    }

    private void mapColumn()
    {
        mapColumnSearch.put("id","ID MENU");
        mapColumnSearch.put("nama","NAMA MENU");
        mapColumnSearch.put("path","PATH MENU");
        mapColumnSearch.put("point","END POINT");
        //mapColumnSearch.get('path')->PATH MENU
    }

//    private void listItemPerPage()
//    {
//        mapItemPerPage.put(5,5);
//        mapItemPerPage.put(10,10);
//        mapItemPerPage.put(15,15);
//        mapItemPerPage.put(20,20);
//        mapItemPerPage.put(25,25);
//        mapItemPerPage.put(30,30);
//    }

    private Page<Menu> getDataByValue(Pageable pageable, String paramColumn, String paramValue)
    {
        if(paramValue.equals(""))
        {
            return menuRepo.findByIsDelete(pageable,(byte) 1);
        }
        if(paramColumn.equals("id"))
        {
            return menuRepo.findByIsDeleteAndIdMenu(pageable,(byte) 1,Long.parseLong(paramValue));
        } else if (paramColumn.equals("nama")) {
            return menuRepo.findByIsDeleteAndNamaMenu(pageable,(byte) 1,paramValue);
        } else if (paramColumn.equals("path")) {
            return menuRepo.findByIsDeleteAndPathMenu(pageable,(byte) 1,paramValue);
        } else if (paramColumn.equals("point")) {
            return menuRepo.findByIsDeleteAndEndPoint(pageable,(byte) 1,paramValue);
        }

        return menuRepo.findByIsDelete(pageable,(byte) 1);
    }

}
