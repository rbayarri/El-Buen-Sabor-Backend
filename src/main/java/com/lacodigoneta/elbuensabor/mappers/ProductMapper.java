package com.lacodigoneta.elbuensabor.mappers;

import com.lacodigoneta.elbuensabor.dto.ImageDto;
import com.lacodigoneta.elbuensabor.dto.category.CategoryDto;
import com.lacodigoneta.elbuensabor.dto.product.ClientProductDto;
import com.lacodigoneta.elbuensabor.dto.product.ProductDto;
import com.lacodigoneta.elbuensabor.dto.product.ProductForOrderDetailDto;
import com.lacodigoneta.elbuensabor.dto.product.SimpleProductDto;
import com.lacodigoneta.elbuensabor.dto.productdetails.ProductDetailDto;
import com.lacodigoneta.elbuensabor.entities.Category;
import com.lacodigoneta.elbuensabor.entities.Image;
import com.lacodigoneta.elbuensabor.entities.Product;
import com.lacodigoneta.elbuensabor.entities.ProductDetail;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ProductMapper {

    private final ModelMapper mapper;

    private final ImageMapper imageMapper;

    private final ProductDetailMapper productDetailMapper;

    public ProductForOrderDetailDto toProductForOrderDetailDto(Product product) {
        ProductForOrderDetailDto productForOrderDetailDto = mapper.map(product, ProductForOrderDetailDto.class);
        ImageDto imageDto = imageMapper.toImageDto(product.getImage());
        productForOrderDetailDto.setImage(imageDto);
        List<ProductDetailDto> productDetailsDto = product.getProductDetails().stream().map(productDetailMapper::toProductDetailDto).toList();
        productForOrderDetailDto.setProductDetails(productDetailsDto);
        return productForOrderDetailDto;
    }

    public ClientProductDto toClientProductDto(Product product) {
        ClientProductDto clientProductDto = mapper.map(product, ClientProductDto.class);

        clientProductDto.setProductDetails(product.getProductDetails()
                .stream()
                .map(productDetailMapper::toProductDetailDto)
                .toList());

        if (Objects.nonNull(product.getImage())) {
            clientProductDto.setImage(imageMapper.toImageDto(product.getImage()));
        }
        return clientProductDto;
    }

    public ProductDto toProductDto(Product product) {
        ProductDto productDto = mapper.map(product, ProductDto.class);
        CategoryDto categoryDto = mapper.map(product.getCategory(), CategoryDto.class);
        productDto.setCategory(categoryDto);

        if (!Objects.isNull(product.getImage())) {
            ImageDto imageDto = imageMapper.toImageDto(product.getImage());
            productDto.setImage(imageDto);
        }

        List<ProductDetailDto> productDetails = product.getProductDetails().stream().map(productDetailMapper::toProductDetailDto).toList();
        productDto.setProductDetails(productDetails);

        return productDto;
    }

    public Product toEntity(ProductDto productDto) {
        Product product = mapper.map(productDto, Product.class);
        product.setCategory(mapper.map(productDto.getCategory(), Category.class));

        List<ProductDetail> productDetails = productDto.getProductDetails().stream()
                .map(productDetailMapper::toProductDetail)
                .toList();

        product.setProductDetails(productDetails);

        product.getProductDetails().forEach(pd -> pd.setProduct(product));

        if (!Objects.isNull(productDto.getImage())) {
            product.setImage(mapper.map(productDto.getImage(), Image.class));
        }

        return product;
    }

    public Product toEntity(SimpleProductDto simpleProductDto) {
        return mapper.map(simpleProductDto, Product.class);
    }

    public SimpleProductDto toSimpleProductDto(Product product) {
        SimpleProductDto map = mapper.map(product, SimpleProductDto.class);
        map.setImage(imageMapper.toImageDto(product.getImage()));
        return map;
    }
}
