package com.juaracoding._01JavaWeb.controller;


import com.juaracoding._01JavaWeb.configuration.OtherConfig;
import com.juaracoding._01JavaWeb.dto.MenuDTO;
import com.juaracoding._01JavaWeb.model.Menu;
import com.juaracoding._01JavaWeb.service.MenuService;
import com.juaracoding._01JavaWeb.utils.ConstantMessage;
import com.juaracoding._01JavaWeb.utils.ManipulationMap;
import com.juaracoding._01JavaWeb.utils.MappingAttribute;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/usrmgmnt")
public class MenuController {

    MenuService menuService;

    @Autowired
    private ModelMapper modelMapper;
    private Map<String,Object> objectMapper = new HashMap<String,Object>();
    private Map<String,String> mapSorting = new HashMap<String,String>();

    private List<Menu> lsCPUpload = new ArrayList<Menu>();

    private String [] strExceptionArr = new String[2];

    private MappingAttribute mappingAttribute = new MappingAttribute();

    public MenuController(MenuService menuService) {
        strExceptionArr[0] = "MenuController";
        mapSorting();
        this.menuService = menuService;
    }

    @GetMapping("/v1/menu/new")
    public String createMenu(Model model,WebRequest request)
    {
        if(OtherConfig.getFlagSessionValidation().equals("y"))
        {
            mappingAttribute.setAttribute(model,objectMapper,request);//untuk set session
            if(request.getAttribute("USR_ID",1)==null){
                return "redirect:/api/check/logout";
            }
        }
        model.addAttribute("menu", new MenuDTO());
        return "menu/create_menu";
    }

    @GetMapping("/v1/menu/edit/{id}")
    public String editMenu(Model model,WebRequest request,@PathVariable("id") Long id)
    {
        if(OtherConfig.getFlagSessionValidation().equals("y"))
        {
            mappingAttribute.setAttribute(model,objectMapper,request);//untuk set session
            if(request.getAttribute("USR_ID",1)==null){
                return "redirect:/api/check/logout";
            }
        }
        objectMapper = menuService.findById(id,request);
        MenuDTO menuDTO = (objectMapper.get("data")==null?null:(MenuDTO) objectMapper.get("data"));
        if((Boolean) objectMapper.get("success"))
        {
            model.addAttribute("menu", menuDTO);
            return "menu/edit_menu";
        }
        else
        {
            model.addAttribute("menu", new MenuDTO());
            return "menu/menu";
        }
    }
    @PostMapping("/v1/menu/new")
    public String newMenu(@ModelAttribute("menu")
                          @Valid MenuDTO menuDTO
            , BindingResult bindingResult
            , Model model
            , WebRequest request
    )
    {
        /* START VALIDATION */
        if(bindingResult.hasErrors())
        {
            model.addAttribute("menu",menuDTO);
            return "menu/create_menu";
        }
        Boolean isValid = true;
        /*
            NANTI DIBUATKAN REGEX UNTUK VALIDASI FORMAT PATH DAN ENDPOINT
         */
        if(!menuDTO.getPathMenu().startsWith("/api/"))
        {
            isValid = false;
            mappingAttribute.setErrorMessage(bindingResult, ConstantMessage.WARNING_MENU_PATH_INVALID);
        }
        if(!menuDTO.getEndPoint().equals(OtherConfig.getUrlEndPointVerify()))
        {
            isValid = false;
            mappingAttribute.setErrorMessage(bindingResult, ConstantMessage.WARNING_MENU_END_POINTS_INVALID);
        }

        if(!isValid)
        {
            model.addAttribute("menu",menuDTO);
            return "menu/create_menu";
        }
        /* END OF VALIDATION */

        Menu menu = modelMapper.map(menuDTO, new TypeToken<Menu>() {}.getType());
        objectMapper = menuService.saveMenu(menu,request);
        if(objectMapper.get("message").toString().equals(ConstantMessage.ERROR_FLOW_INVALID))//AUTO LOGOUT JIKA ADA PESAN INI
        {
            return "redirect:/api/check/logout";
        }

        if((Boolean) objectMapper.get("success"))
        {
            mappingAttribute.setAttribute(model,objectMapper);
            model.addAttribute("menu",new MenuDTO());

            return "menu/menu";
        }
        else
        {
            mappingAttribute.setErrorMessage(bindingResult,objectMapper.get("message").toString());
            model.addAttribute("menu",new MenuDTO());
            return "menu/create_menu";
        }
    }

    @PostMapping("/v1/menu/edit/{id}")
    public String doRegis(@ModelAttribute("menu")
                          @Valid MenuDTO menuDTO
            , BindingResult bindingResult
            , Model model
            , WebRequest request
            , @PathVariable("id") Long id
    )
    {
        /* START VALIDATION */
        if(bindingResult.hasErrors())
        {
            model.addAttribute("usr",menuDTO);
            return "menu/create_menu";
        }
        Boolean isValid = true;
        /*
            NANTI DIBUATKAN REGEX UNTUK VALIDASI FORMAT PATH DAN ENDPOINT
         */
        if(!menuDTO.getPathMenu().startsWith("/api/"))
        {
            isValid = false;
            mappingAttribute.setErrorMessage(bindingResult, ConstantMessage.WARNING_MENU_PATH_INVALID);
        }
        if(!menuDTO.getEndPoint().equals(OtherConfig.getUrlEndPointVerify()))
        {
            isValid = false;
            mappingAttribute.setErrorMessage(bindingResult, ConstantMessage.WARNING_MENU_END_POINTS_INVALID);
        }

        if(!isValid)
        {
            model.addAttribute("menu",menuDTO);
            return "menu/create_menu";
        }
        /* END OF VALIDATION */

        Menu menu = modelMapper.map(menuDTO, new TypeToken<Menu>() {}.getType());
        objectMapper = menuService.updateMenu(id,menu,request);
        if(objectMapper.get("message").toString().equals(ConstantMessage.ERROR_FLOW_INVALID))//AUTO LOGOUT JIKA ADA PESAN INI
        {
            return "redirect:/api/check/logout";
        }

        if((Boolean) objectMapper.get("success"))
        {
            mappingAttribute.setAttribute(model,objectMapper);
            model.addAttribute("menu",new MenuDTO());

            return "menu/menu";
        }
        else
        {
            mappingAttribute.setErrorMessage(bindingResult,objectMapper.get("message").toString());
            model.addAttribute("menu",new MenuDTO());
            return "menu/create_menu";
        }
    }


    @GetMapping("/v1/menu/default")
    public String getDefaultData(Model model,WebRequest request)
    {
        if(OtherConfig.getFlagSessionValidation().equals("y"))
        {
            mappingAttribute.setAttribute(model,objectMapper,request);//untuk set session
            if(request.getAttribute("USR_ID",1)==null){
                return "redirect:/api/check/logout";
            }
        }
        Pageable pageable = PageRequest.of(0,5, Sort.by("idMenu"));
        objectMapper = menuService.findAllMenu(pageable,request);
        mappingAttribute.setAttribute(model,objectMapper,request);

        model.addAttribute("menu",new MenuDTO());
        model.addAttribute("sortBy","idMenu");
        model.addAttribute("asc","asc");
        model.addAttribute("columnFirst","");
        model.addAttribute("valueFirst","");
        model.addAttribute("sizeComponent",5);
        return "/menu/menu";
    }

    @GetMapping("/v1/menu/fbpsb/{page}/{sort}/{sortby}")
    public String findBYMenu(
            Model model,
            @PathVariable("page") Integer pagez,
            @PathVariable("sort") String sortz,
            @PathVariable("sortby") String sortzBy,
            @RequestParam String columnFirst,
            @RequestParam String valueFirst,
            @RequestParam String sizeComponent,
            WebRequest request
    ){
        pagez = pagez - 1;
        sortzBy = mapSorting.get(sortzBy);
        sortzBy = sortzBy==null?"idMenu":sortzBy;
        Pageable pageable = PageRequest.of(pagez,Integer.parseInt(sizeComponent), Sort.by(sortzBy));
        objectMapper = menuService.findByPage(pageable,request,columnFirst,valueFirst);
        mappingAttribute.setAttribute(model,objectMapper,request);
        model.addAttribute("menu",new MenuDTO());
        model.addAttribute("sortBy", ManipulationMap.getKeyFromValue(mapSorting,sortzBy));
        model.addAttribute("columnFirst",columnFirst);
        model.addAttribute("valueFirst",valueFirst);
        model.addAttribute("sizeComponent",sizeComponent);

        return "/menu/menu";
    }

    private void mapSorting()
    {
        mapSorting.put("id","idMenu");
        mapSorting.put("nama","namaMenu");
        mapSorting.put("path","pathMenu");
        mapSorting.put("ep","endPoint");
    }

}