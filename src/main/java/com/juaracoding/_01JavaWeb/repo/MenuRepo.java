package com.juaracoding._01JavaWeb.repo;

import com.juaracoding._01JavaWeb.model.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MenuRepo extends JpaRepository<Menu,Long> {

    Page<Menu> findByIsDelete(Pageable page , byte byteIsDelete);
    Page<Menu> findByIsDeleteAndNamaMenu(Pageable page , byte byteIsDelete,String values);
    Page<Menu> findByIsDeleteAndPathMenu(Pageable page , byte byteIsDelete,String values);
    Page<Menu> findByIsDeleteAndEndPoint(Pageable page , byte byteIsDelete, String values);
    Page<Menu> findByIsDeleteAndIdMenu(Pageable page , byte byteIsDelete, Long values);

}