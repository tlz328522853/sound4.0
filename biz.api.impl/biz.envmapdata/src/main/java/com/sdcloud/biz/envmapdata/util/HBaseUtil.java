package com.sdcloud.biz.envmapdata.util;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.RegionLocator;
import org.apache.hadoop.hbase.client.Table;

public class HBaseUtil {
	
	private static Configuration conf;
	
	private static Connection hConnection;
	
	static {
		
		Configuration conf = HBaseConfiguration.create();
		Path path = new Path("./conf/hbase-site.xml");
		conf.addResource(path);

		try {
			hConnection = ConnectionFactory.createConnection(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Configuration getConf() {
		return conf;
	}
	
	public static Connection getConnection() {
		return hConnection;
	}
	/**
	 * scores
	 * @param tableName
	 * @return
	 * @throws IOException
	 */
	public static Table getHTable(String tableName) throws IOException {
		return hConnection.getTable(TableName.valueOf(tableName));
	}
	
	public static void main(String args[]){
		try {
			for(TableName table : getConnection().getAdmin().listTableNames()){
				System.out.println(table.getNameAsString());
				RegionLocator r = getConnection().getRegionLocator(TableName.valueOf(table.getNameAsString()));
				for(HRegionLocation every : r.getAllRegionLocations()){
					System.out.println(every.getHostnamePort());
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
