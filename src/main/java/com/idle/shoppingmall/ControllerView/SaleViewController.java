package com.idle.shoppingmall.ControllerView;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class SaleViewController {

    @GetMapping("/FE/sale")
    String sale() {

        return "/FE/sale";
    } // sale

} // end class
