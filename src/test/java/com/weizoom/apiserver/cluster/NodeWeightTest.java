/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-6-8
 */
package com.weizoom.apiserver.cluster;

import static org.junit.Assert.*;
import org.junit.Test;

public class NodeWeightTest {
	
	@Test
	public void testWeight() {
		//��ʼ��Ӧ�ö���ͬ
		NodeWeight nodeWeight1 = new NodeWeight();
		NodeWeight nodeWeight2 = new NodeWeight();
		assertEquals(nodeWeight1.getWeight(), nodeWeight2.getWeight());
		
		//������������
		for (int i = 0; i < 49; i++) {
			nodeWeight1.increaseInProgress();
		}
		assertTrue(nodeWeight2.getWeight() > nodeWeight1.getWeight());
		
		
		//�������ɸ�ʧ��
		NodeWeight nodeWeight3 = new NodeWeight();
		for (int i = 0; i < 1; i++) {
			nodeWeight3.increaseInProgress();
		}
		for (int i = 0; i < 1; i++) {
			nodeWeight3.increaseClusterError();
		}
		nodeWeight3.increaseSuccess(2000);//�������һ����Ӱ��
		int weight = nodeWeight3.getWeight();
		assertTrue(nodeWeight2.getWeight() > nodeWeight3.getWeight());
	
		//���ɸ��ɹ�
		for (int i = 0; i < 40; i++) {
			nodeWeight3.increaseInProgress();
		}
		for (int i = 0; i < 40; i++) {
			nodeWeight3.increaseSuccess(0);
		}
		assertTrue(nodeWeight3.getWeight() > weight);

		//���һ����Ӱ��
		NodeWeight nodeWeight4 = new NodeWeight();
		NodeWeight nodeWeight5 = new NodeWeight();
		nodeWeight4.increaseInProgress();
		nodeWeight4.increaseClusterError();
		nodeWeight4.increaseInProgress();
		nodeWeight4.increaseSuccess(0);
		
		nodeWeight5.increaseInProgress();
		nodeWeight5.increaseSuccess(0);
		nodeWeight5.increaseInProgress();
		nodeWeight5.increaseClusterError();
		assertTrue(nodeWeight4.getWeight() > nodeWeight5.getWeight());
	}
}
