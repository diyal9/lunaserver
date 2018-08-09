package luna.services.payapp;

public interface ConstantsLuna {

	/**
	 * 请求订单参数有空的值
	 */
	public static final String LUNA_STATUS_PARAMHADNULL = "100";
	
	/**
	 * APPKEY不正确
	 */
	public static final String LUNA_STATUS_APPKEYERROR = "101";
	
	/**
	 * 请求不正确
	 */
	public static final String LUNA_STATUS_HTTPERROR = "102";
	
	/**
	 * 平台验证失败
	 */
	public static final String LUNA_STATUS_PLATVALIDERROR= "103";
	
	/**
	 * 已经回复SDK服务器
	 */
	public static final String LUNA_STATUS_HADRETURNSDKSERVER = "104";
	
	/**
	 * 已经成功处理订单
	 */
	public static final String LUNA_STATUS_HANDLESUCCESS = "105";
	
	/**
	 * 无效订单
	 */
	public static final String ORDER_STATUS_NOORDER = "201";
	
	/**
	 * 订单已经处理
	 */
	public static final String ORDER_STATUS_ORDERHADHANDLE = "202";

}
