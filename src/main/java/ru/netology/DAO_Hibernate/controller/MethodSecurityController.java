package ru.netology.DAO_Hibernate.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.annotation.security.RolesAllowed;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/method-security")
public class MethodSecurityController {

    // 1. Только для роли READ (используем @Secured)

    @Secured("ROLE_READ")
    @GetMapping("/read-only")
    public String readOnly() {
        return "Доступно пользователям с ролью READ";
    }

    // 2. Только для роли WRITE (используем @RolesAllowed из JSR-250)

    @RolesAllowed("WRITE")
    @GetMapping("/write-only")
    public String writeOnly() {
        return "Доступно пользователям с ролью WRITE";
    }

    // 3. Если есть хотя бы одна из ролей WRITE или DELETE (pre/post аннотации)
    //    hasAnyRole автоматически добавляет ROLE_ → проверяет ROLE_WRITE или ROLE_DELETE
    @PreAuthorize("hasAnyRole('ROLE_WRITE','ROLE_DELETE')")
    @GetMapping("/write-or-delete")
    public String writeOrDelete() {
        return "Доступно пользователям с ролью WRITE или DELETE";
    }

    // 4. Доступно, только если username из query-параметра совпадает
    //    с именем текущего аутентифицированного пользователя
    @PreAuthorize("#username == authentication.name")
    @GetMapping("/self")
    public String self(@RequestParam("username") String username) {
        return "Доступно только пользователю с именем: " + username;
    }
    @GetMapping("/whoami")
    public String whoami(Authentication authentication) {
        return "User: " + authentication.getName()
                + ", Roles: " + authentication.getAuthorities();
    }
}