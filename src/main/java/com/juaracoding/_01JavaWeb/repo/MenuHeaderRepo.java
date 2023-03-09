package com.juaracoding._01JavaWeb.repo;


import com.juaracoding._01JavaWeb.model.MenuHeader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuHeaderRepo extends JpaRepository<MenuHeader,Long> {

    Page<MenuHeader> findByIsDelete(Pageable page , byte byteIsDelete);
    Page<MenuHeader> findByIsDeleteAndIdMenuHeader(Pageable pageable, Byte isDelete, String valueparamValue);
    Page<MenuHeader> findByIsDeleteAndNamaMenuHeader(Pageable pageable,Byte isDelete,String valueparamValue);
    Page<MenuHeader> findByIsDeleteAndDeskripsiMenuHeader(Pageable pageable,Byte isDelete,String valueparamValue);

}
