/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-5-16
 */

/**
 * <p><i>WeizoomApiserver :: apiserver</i>��˾Apiserver��ܻ���jetty��ʵ��
 * 
 * <p>
 * �ÿ��ʹ��Ƕ��ʽ��jetty��Ϊhttp���洦��ͨѶ������{@code URL}·�ɵ��ض�{@link com.weizoom.apiserver.api.Api Api}��
 * ����{@code Api}������Ӧ����֮����װ{@code Api}�Ľ�����ݷ��ظ�{@code Api}������<br>
 * 
 * ͨѶЭ�����£�<br>
 * 
 * �ڸÿ���а�{@link com.weizoom.apiserver.api.Api Api}����Ϊ��һ���ض���Դ�Ĳ����ӿ�,
 * ��һ��{@code Api}�ķ��ʵ�{@code URL}����������Ҫ���ʵ���Դ���Լ������еĲ������ÿ����֧�ֵĶ�
 * ��Դ�Ĳ���������<br>
 * <ul>
 * <li>��ȡ</li>
 * <li>ɾ��</li>
 * <li>�޸�</li>
 * <li>����</li>
 * </ul>
 * 
 * <br>
 * �����ͨ��{@code URL}��Я���Ķ���Դ�Ĳ�����Ϣ��get,delete,modify,create�����߷���Э�飨GET, 
 * DELETE, PUT, POST��ʶ�����Դ�Ĳ�����Ϣ
 * 
 * ���磺<br>
 * <i>http://${host}:${port}/api/tweet/get/?id=1</i><br>
 * ����<br>
 * <i>GET http://${host}:${port}/api/tweet/?id=1</i><br>
 * ��Ϊ������Դ<i>tweet</i>, ��������Ϊ��ȡ����ȡidΪ1��tweet��
 * <br />
 * 
 * ��ܷ��ظ��û������ݸ�ʽΪJson�����£�
 * <pre>
 * {
 *    "code":200, //(����Ϊ200,400,500)��200��ʾ�����ɹ���400��ʾ��������500��ʾ�ڲ��쳣
 *    "errMsg":"������Ϣ", //����ʾ�Ĵ�����Ϣ
 *    "innerErrMsg":"", //��debug����ϸ������Ϣ�����������쳣��ջ��Ϣ
 *    "data":{} //Api����������ʽΪJson
 * }
 * </pre>
 */
package com.weizoom.apiserver;