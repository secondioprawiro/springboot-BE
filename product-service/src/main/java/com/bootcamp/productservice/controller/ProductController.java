package com.bootcamp.productservice.controller;

import com.bootcamp.productservice.dto.request.ClaimStarterRequest;
import com.bootcamp.productservice.dto.response.BaseResponse;
import com.bootcamp.productservice.dto.response.ResCreatePokemonDto;
import com.bootcamp.productservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/sync")
    public ResponseEntity<BaseResponse<Object>> syncData(){
        productService.syncDataByThirdPartyApi()
                .thenAccept(result -> log.info("Update dari background: {}", result))
                .exceptionally(ex ->{
                    log.error("Update dari background: {}", ex.getMessage());
                    return null;
                });
        return ResponseEntity.ok(BaseResponse.success(null, "Permintaan sedang diproses"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ResCreatePokemonDto>> getProductById(
        @PathVariable String id
    ){
        ResCreatePokemonDto resCreatePokemonDto =productService.getPokemonById(id);
        return ResponseEntity.ok(BaseResponse.success(resCreatePokemonDto, "Get pokemon ID: " + id));
    }

    @PostMapping("/claim-starter")
    public ResponseEntity<BaseResponse<List<ResCreatePokemonDto>>> claimStarter(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody ClaimStarterRequest request
    ){
        List<ResCreatePokemonDto> claimedPokemons = productService.claimStarter(userId, request);
        return ResponseEntity.ok(BaseResponse.success(claimedPokemons, "Berhasil klaim Pokemon starter"));
    }

    @GetMapping("/my-pokemon")
    public ResponseEntity<BaseResponse<List<ResCreatePokemonDto>>> getMyPokemon(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) String rarity
    ){
        List<ResCreatePokemonDto> myPokemons = productService.getMyPokemon(userId, rarity);

        return ResponseEntity.ok(BaseResponse.success(myPokemons, "Berhasil mengambil daftar pokemon milik user"));
    }

    @GetMapping("/user/{targetUserId}/pokemon")
    public ResponseEntity<BaseResponse<List<ResCreatePokemonDto>>> getPokemonByUserId(
            @RequestHeader("X-User-Id") String myUserId,
            @PathVariable("targetUserId") Long targetUserId
    ){
        List<ResCreatePokemonDto> targetPokemons = productService.getPokemonByUserId(targetUserId);

        return ResponseEntity.ok(BaseResponse.success(targetPokemons, "Berhasil mengambil koleksi pokemon milik user " + targetUserId));
    }
}
