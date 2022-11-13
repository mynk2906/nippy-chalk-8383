package com.Dao.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.Dao.Utility.DBUtil;
import com.Dao.models.Vendor;

public class VendorDaoImpl implements VendorDao {

	@Override
	public String registerVendor(Vendor vendor) {

		String status = "Registration Failed!!";

		try  (Connection con = DBUtil.provideConnection()) {

			PreparedStatement pst = con.prepareStatement("select * from vendor where vid=? and vpassword = ?");

			pst.setString(1, vendor.getVid());
			pst.setString(2, vendor.getVpassword());

			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				status = "Registration Declined! Vendor Id already Registered";
			} else {

				try {

					PreparedStatement ps = con.prepareStatement("insert into vendor(vid,vpassword) values(?,?)");

					ps.setString(1, vendor.getVid());
					ps.setString(2, vendor.getVpassword());

					int k = ps.executeUpdate();

					if (k > 0)
						// update successful
						status = "Registration Successful. \nYour Vendor id: " + vendor.getVid()
								+ "\nThanks For Registration";
				}

				catch (SQLException e) {
					status = "Error: " + e.getErrorCode() + " : " + e.getMessage();
				}
			}
		} catch (SQLException e) {
			status = "Error: " + e.getErrorCode() + " : " + e.getMessage();
		}

		return status;

	}

	@Override
	public List<Vendor> getAllVendors() {

		List<Vendor> vendorList = new ArrayList<Vendor>();

		try (Connection con=DBUtil.provideConnection()) {
			PreparedStatement ps = con.prepareStatement("select * from vendor");
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Vendor vendor = new Vendor(rs.getString("vid"), rs.getString("vpassword"), rs.getString("vname"),
						rs.getString("vemail"), rs.getString("vmob"), rs.getString("company"), rs.getString("address"));
				vendorList.add(vendor);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return vendorList;
	}

	@Override
	public boolean validatePassword(String vid, String password) {
		boolean flag = false;
		
		try(Connection con=DBUtil.provideConnection()) {
			PreparedStatement pst = con.prepareStatement("select * from vendor where vid=? and vpassword=?");

			pst.setString(1, vid);
			pst.setString(2, password);

			ResultSet rs = pst.executeQuery();

			if (rs.next())
				flag = true;

		} catch (SQLException e) {
			e.printStackTrace();
		} 

		return flag;
	}

	@Override
	public String updateProfile(Vendor vendor) {

		String status = "Account Updation Failed";

		String vendorId = vendor.getVid();
		String password = vendor.getVpassword();

		VendorDao dao = new VendorDaoImpl();

		if (!dao.validatePassword(vendorId, password)) {
			status = status + "\nYou Have Entered Wrong Password!";
			return status;
		}
		
		try (Connection con=DBUtil.provideConnection()) {
			PreparedStatement ps = con.prepareStatement("update vendor set vname=?,vmob=?,vemail=?,company=?,address=? where vid=?");

			ps.setString(1, vendor.getVname());
			ps.setString(2, vendor.getVmob());
			ps.setString(3, vendor.getVemail());
			ps.setString(4, vendor.getCompany());
			ps.setString(5, vendor.getAddress());
			ps.setString(6, vendor.getVid());

			int x = ps.executeUpdate();

			if (x > 0) {
				status = "Account Updated Successfully!";
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return status;

	}

	@Override
	public String changePassword(String vendorId, String oldPassword, String newPassword) {

		String status = "Password Updation failed!";

		VendorDao dao = new VendorDaoImpl();

		if (!dao.validatePassword(vendorId, oldPassword)) {

			status = status + "You Have Entered Wrong Old Password!";

			return status;
		}

		try (Connection con=DBUtil.provideConnection()) {

			PreparedStatement ps = con.prepareStatement("update vendor set password = ? where vid=?");
			ps.setString(1, newPassword);
			ps.setString(2, vendorId);

			int x = ps.executeUpdate();

			if (x > 0)
				status = "Password Updated Successfully!";

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return status;
	}

	@Override
	public Vendor getVendorDataById(String vendorId) {

		Vendor vendor = null;

		try (Connection con=DBUtil.provideConnection()) {
			
			PreparedStatement ps = con.prepareStatement("select * from vendor where vid=?");
			ps.setString(1, vendorId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				vendor = new Vendor(rs.getString("vid"), rs.getString("vpassword"), rs.getString("vname"),
						rs.getString("vemail"), rs.getString("vmob"), rs.getString("company"), rs.getString("address"));

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return vendor;
	}

}
