package com.rjproj.memberapp.controller;

import com.rjproj.memberapp.dto.GetMenuResponse;
import com.rjproj.memberapp.dto.MemberResponse;
import com.rjproj.memberapp.service.MemberService;
import com.rjproj.memberapp.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<GetMenuResponse> getMenu() {
        return ResponseEntity.ok(menuService.getMenu());
    }
}
