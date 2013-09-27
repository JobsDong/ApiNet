/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-5-16
 */

/**
 * <p><i>WeizoomApiserver :: apiserver</i>公司Apiserver框架基于jetty的实现
 * 
 * <p>
 * 该框架使用嵌入式的jetty作为http引擎处理通讯，根据{@code URL}路由到特定{@link com.weizoom.apiserver.api.Api Api}，
 * 调用{@code Api}进行相应处理，之后组装{@code Api}的结果数据返回给{@code Api}调用者<br>
 * 
 * 通讯协议如下：<br>
 * 
 * 在该框架中把{@link com.weizoom.apiserver.api.Api Api}定义为对一个特定资源的操作接口,
 * 对一个{@code Api}的访问的{@code URL}中描述了需要访问的资源，以及所进行的操作，该框架中支持的对
 * 资源的操作包括：<br>
 * <ul>
 * <li>获取</li>
 * <li>删除</li>
 * <li>修改</li>
 * <li>创建</li>
 * </ul>
 * 
 * <br>
 * 框架中通过{@code URL}中携带的对资源的操作信息（get,delete,modify,create）或者访问协议（GET, 
 * DELETE, PUT, POST）识别对资源的操作信息
 * 
 * 例如：<br>
 * <i>http://${host}:${port}/api/tweet/get/?id=1</i><br>
 * 或者<br>
 * <i>GET http://${host}:${port}/api/tweet/?id=1</i><br>
 * 意为访问资源<i>tweet</i>, 所做操作为获取（获取id为1的tweet）
 * <br />
 * 
 * 框架返回给用户的数据格式为Json，如下：
 * <pre>
 * {
 *    "code":200, //(可以为200,400,500)，200表示操作成功，400表示参数错误，500表示内部异常
 *    "errMsg":"错误信息", //供显示的错误信息
 *    "innerErrMsg":"", //供debug的详细错误信息，包括整个异常堆栈信息
 *    "data":{} //Api处理结果，格式为Json
 * }
 * </pre>
 */
package com.weizoom.apiserver;