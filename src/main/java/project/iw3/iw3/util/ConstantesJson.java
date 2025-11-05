package project.iw3.iw3.util;

public class ConstantesJson {

	
	public static final String[] ORDEN_PRESET_ATTRIBUTES = {"preset", "order_preset"};
	public static final String[] ORDEN_NUMERORDEN_ATTRIBUTES = {"number", "order_number", "order"};
	public static final String[] ORDER_ESTIMATED_DATE_ATTRIBUTES = {"estimated_date", "estimated_date_order", "estimated_time", "estimated_time_order"};

	
	
	
	 // Constantes para el nodo Driver
	public static final String[] CHOFER_NODE_ATTRIBUTES = {"driver", "Driver", "choffer", "Choffer"};
    public static final String[] DRIVER_ID_ATTRIBUTES = {"id", "id_driver", "code", "driver_code"};
    public static final String[] DRIVER_NOMBRE_ATTRIBUTES = {"name", "driver_name", "driver"};
    public static final String[] DRIVER_APELLIDO_ATTRIBUTES = {"lastname", "driver_lastname", "last_name", "driver_last_name"};
    public static final String[] DRIVER_DNI_ATTRIBUTES = {"driver_document", "driver_document_number", "document"};

    
 // Constantes para el nodo Truck
    public static final String[] CAMION_NODE_ATTRIBUTES = {"truck", "Truck", "vehicle", "Vehicle"};
    public static final String[] CAMION_ID_ATTRIBUTES = {"id", "id_truck", "code", "truck_code"};
    public static final String[] CAMION_PATENTE_ATTRIBUTES = {"patente","truck_plate", "truck_plate_number", "license_plate", "truck_license_plate", "licence_plate", "truck_licence_plate"};
    public static final String[] CAMION_DESCRIPCION_ATTRIBUTES = {"description", "truck_description"};
    
 // Constantes para el nodo Tanker
    public static final String[] CISTERNA_NODE_ATTRIBUTES = {"tanks"};
    public static final String[] CISTERNA_CAPACIDAD_LITROS_ATTRIBUTES = {"capacity_liters", "capacity"};
    public static final String[] CISTERNA_ID_ATTRIBUTES = {"id", "id_tanker", "code", "tanker_code"};
    public static final String[] CISTERA_LICENCIA_ATTRIBUTES = {"license", "license_plate", "plate", "licence", "licence_plate"};
    
 // Constantes para el nodo Customer
    public static final String[] CLIENTE_NODE_ATTRIBUTES = {"customer", "Customer", "client", "Client"};
    public static final String[] CLIENTE_ID_ATTRIBUTES = {"id", "id_customer", "code", "customer_code"};
    public static final String[] CLIENTE_NOMBRE_ATTRIBUTES = {"name", "customer_name", "business_name", "customer"};
    public static final String[] CLIENTE_EMAIL_ATTRIBUTES = {"mail", "email", "contact", "mail_contact"};

    // Constantes para el nodo Product
    public static final String[] PRODUCTO_NODE_ATTRIBUTES = {"product", "Product", "gas", "Gas", "fuel", "Fuel"};
    public static final String[] PRODUCTO_ID_ATTRIBUTES = {"id", "id_product", "code", "product_code"};
    public static final String[] PRODUCTO_NOMBRE_ATTRIBUTES = {"product", "product_name"};
    public static final String[] PRODUCTO_DESCRIPCION_ATTRIBUTES = {"description"};



}
