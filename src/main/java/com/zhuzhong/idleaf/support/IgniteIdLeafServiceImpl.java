/**
 * 
 */
package com.zhuzhong.idleaf.support;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteAtomicSequence;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.zk.TcpDiscoveryZookeeperIpFinder;
import org.springframework.beans.factory.InitializingBean;

import com.zhuzhong.idleaf.IdLeafService;

/**
 * @author sunff
 *
 */

public class IgniteIdLeafServiceImpl implements IdLeafService, InitializingBean {

	private static final String datePattern = "yyyyMMddHHmmss";

	@Override
	public Long getId() {

		return seq.getAndIncrement();
	}

	private IgniteAtomicSequence seq;

	
	private String zkAddress;
	private String bizTag;
	
	
	public void setBizTag(String bizTag) {
		this.bizTag = bizTag;
	}

	public void setZkAddress(String zkAddress) {
		this.zkAddress = zkAddress;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub

		SimpleDateFormat s = new SimpleDateFormat(datePattern);
		Long initValue = Long.valueOf(s.format(new Date())) * 100000;
		TcpDiscoverySpi spi = new TcpDiscoverySpi();
		TcpDiscoveryZookeeperIpFinder ipFinder = new TcpDiscoveryZookeeperIpFinder();
		// Specify ZooKeeper connection string.
		ipFinder.setZkConnectionString(this.zkAddress);
		spi.setIpFinder(ipFinder);
		IgniteConfiguration cfg = new IgniteConfiguration();
		// Override default discovery SPI.
		cfg.setDiscoverySpi(spi);
		// Start Ignite node.
		Ignite ignite = Ignition.start(cfg);

		// Ignite ignite = Ignition.start();
		seq = ignite.atomicSequence(bizTag, // 序列名
				initValue, // 初始值
				true// 如果序列不存在则创建
		);

	}

}
