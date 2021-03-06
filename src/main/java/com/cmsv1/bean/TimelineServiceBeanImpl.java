/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cmsv1.bean;
import com.cmsv1.sqlconnection.SQLiteConfiguration;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.util.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

public class TimelineServiceBeanImpl implements TimelineServiceBean
{
    
    public List<TimeBalanceProp> getTimeBalance(String timeLineType)
    {
        SQLiteConfiguration _sql = new SQLiteConfiguration();
        List<TimeBalanceProp> propList = new ArrayList<>();
        String query = "";
        ResultSet rs = null;
        try
        {
            if(timeLineType.equals("all"))
            {
                query = "select tb_id, tb_customername, tb_time, tb_date, replace(replace(tb_active,'0','used'),'1','unused') as tb_active, tb_comments, tb_createby, tb_updateby from time_balance order by tb_id desc;";
            }
            
            else if(timeLineType.equals("unused"))
            {
                query = "select tb_id, tb_customername, tb_time, tb_date, replace(tb_active,'1','unused') as tb_active, tb_comments, tb_createby, tb_updateby from time_balance where tb_active='1' order by tb_id desc;";
            }
            
            else if(timeLineType.equals("used"))
            {
                query = "select tb_id, tb_customername, tb_time, tb_date, replace(tb_active,'0','used') as tb_active, tb_comments, tb_createby, tb_updateby from time_balance where tb_active='0' order by tb_id desc;";
            }
            rs = _sql.myStmt.executeQuery(query);
            while(rs.next())
            {
                TimeBalanceProp tb = new TimeBalanceProp();
                tb.setTb_id(rs.getString("tb_id"));
                tb.setTb_time(rs.getString("tb_time"));
                tb.setTb_date(rs.getString("tb_date"));
                tb.setTb_customername(rs.getString("tb_customername"));
                tb.setTb_type(rs.getString("tb_active"));
                tb.setTb_comments(rs.getString("tb_comments"));
                tb.setTb_createBy(rs.getString("tb_createby"));
                tb.setTb_updateBy(rs.getString("tb_updateby"));
                propList.add(tb);
            }
            rs.close();
            _sql.closeConnections();
        }
        catch (Exception e)
        {
            
        }
        return propList;
    }
    
    public void createTimeBalance(String adminFullName, String custName, String timeHour, String timeMinute, String comments)
    {
        SQLiteConfiguration _sql = new SQLiteConfiguration();
        try
        {
            //SimpleDateFormat dateNow = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            String time = "";
            custName = WordUtils.capitalizeFully(custName);
            SimpleDateFormat sdfDate = new SimpleDateFormat("MMMMM d, yyyy - h:mma (EEEE)");//dd/MM/yyyy
            Date now = new Date();
            String dateNow = sdfDate.format(now);
            if(timeMinute.equals("0"))
            {
                time = timeHour + "hour";
            }
            
            else if(timeHour.equals("0"))
            {
                time = timeMinute + "minute";
            }
            
            else
            {
                time = timeHour + "hour " + timeMinute + "minute";
            }
            
            
            _sql.myStmt.executeQuery("insert into time_balance(tb_customername, tb_time, tb_date, tb_comments, tb_createby) values ('" + custName + "','" + time + "','" + dateNow + "','" + (comments.equals("") || comments == null ? "No Comment" : comments) + "','" + adminFullName + "');");
            _sql.closeConnections();
        }
        catch (Exception e)
        {
            e.getStackTrace();
        }
    }
    
    public void deleteTimeBalance(String adminFullName, String id)
    {
        SQLiteConfiguration _sql = new SQLiteConfiguration();
        try
        {
            if(!id.equals(""))
            {
                _sql.myStmt.executeQuery("update time_balance set tb_active='0', tb_updateby='" + adminFullName + "' where tb_id='" + id + "';");
                _sql.closeConnections();
            }
        }
        catch (Exception e)
        {
            e.getStackTrace();
        }
    }
    
    public List<String> readCustomers()
    {
        SQLiteConfiguration _sql = new SQLiteConfiguration();
        List<String> customerNames = new ArrayList<>();
        ResultSet rs = null;
        try
        {
            rs = _sql.myStmt.executeQuery("select ctm_fullname from customers where ctm_active='1' order by ctm_fullname asc;");
            
            while(rs.next())
            {
                customerNames.add(rs.getString("ctm_fullname"));
            }
            rs.close();
            _sql.closeConnections();
        }
        catch (Exception e)
        {
        }
        return customerNames;
    }
}
