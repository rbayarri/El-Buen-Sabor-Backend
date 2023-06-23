package com.lacodigoneta.elbuensabor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConstants {

    public static String ORIGIN_APP;

    @Value("${web.origin}")
    public void setOriginApp(String originApp) {
        ORIGIN_APP = originApp;
    }

    public static final String NULL_VALIDATION_MESSAGE = "Campo no permitido";

    public static final String NOT_NULL_VALIDATION_MESSAGE = "Campo requerido";

    public static final String NOT_EMPTY_VALIDATION_MESSAGE = "Campo no puede ser vacío";

    public static final String EMAIL_VALIDATION_MESSAGE = "Correo electrónico no válido";

    public static final String POSITIVE_OR_ZERO_VALIDATION_MESSAGE = "Campo debe ser mayor o igual a 0";

    public static final String POSITIVE_VALIDATION_MESSAGE = "Campo debe ser mayor a 0";

    public static final String PASSWORD_PATTERN_VALIDATION_MESSAGE = "La contraseña debe contener al menos 1 letra mayúscula, 1 letra minúscula, 1 número y 1 caracter especial";

    public static final String SIZE_VALIDATION_MESSAGE = "El campo debe contener al menos un elemento";

    public static final String DUPLICATED_INGREDIENTS_VALIDATION_MESSAGE = "El campo contine ingredientes duplicados";

    public static final String INVALID_CREDENTIALS = "Usuario y/o contraseña inválidos";

    public static final String NOT_LOGGED_USER = "El usuario no se encuentra logueado";

    public static final String INVALID_TOKEN = "Token no válido";

    public static final String INCOMPATIBLE_MEASUREMENT_UNIT_TYPE = "Tipos de unidades de medida incompatibles entre ";

    public static final String INCOMPATIBLE_PARENT_CATEGORY_TYPE = "El tipo de rubro/categoría no coincide entre elemenento padre e hijo";

    public static final String NOT_CONTAINER_PARENT_CATEGORY = "El rubro/categoría padre no puede contener otros rubros/categorías";

    public static final String CHILD_PARENT_CATEGORY = "El rubro/categoría padre no puede ser uno de sus elementos hijos";

    public static final String INACTIVE_PARENT_CATEGORY = "El rubro/categoría padre debe estar activo";

    public static final String ACTIVE_CHILDREN_CATEGORY = "El rubro/categoría contiene elementos activos";

    public static final String NOT_FOR_PRODUCTS_CATEGORY = "El rubro/categoría no puede ser usado para productos";

    public static final String NOT_FOR_INGREDIENTS_CATEGORY = "El rubro/categoría no puede ser usado para ingredientes";

    public static final String INACTIVE_CATEGORY = "El rubro/categoría se encuentra inactivo";

    public static final String INACTIVE_INGREDIENT = "El ingrediente no se encuentra activo";

    public static final String INVALID_DATE_FOR_INGREDIENT_PURCHASE = "Existen compras con fecha posterior para el ingrediente indicado";

    public static final String MEASUREMENT_UNIT_CANNOT_BE_CHANGED = "La unidad de medida no puede ser modificada";

    public static final String USED_INGREDIENT = "El ingrediente es utilizado por productos activos";

    public static final String CONTAINER_CATEGORY = "El rubro/categoría solamente puede contener otros rubros/categorías";

    public static final String FORBIDDEN = "No posee permisos para la operación solicitada";

    public static final String FORBIDDEN_OPERATION = "Operación no permitida";

    public static final String LOCAL_PICK_UP = "Orden a ser retirada al local no puede enviarse a Delivery";

    public static final String PRODUCTS_TO_COOK = "Existen productos por elaborar";

    public static final String NO_PRODUCTS_TO_COOK = "No existen productos por elaborar";

    public static final String INSUFFICIENT_STOCK_FOR_ORDER = "Stock insuficiente";

    public static final String INACTIVE_INGREDIENTS = "Existen ingredientes inactivos";

    public static final String USED_PRODUCT = "El producto se encuentra en una orden pendiente";

    public static final String NEW_STOCK_OR_STOCK_LOSS_OR_STOCK_GAIN = "Solamente se debe indicar uno de los siguientes campos: 'newStock', 'stockLoss' y 'stockGain'";

    public static final String INACTIVE_ADDRESS = "La dirección se encuentra inactiva";

    public static final String INACTIVE_PHONE_NUMBER = "El número de teléfono se encuentra inactivo";

    public static final String UNCONFIRMED_EMAIL = "Necesita confirmar el correo electrónico antes de realizar un pedido";

    public static final String CATEGORY_WITH_CHILDREN = "Category has children, remove them before set it as not container";


}
