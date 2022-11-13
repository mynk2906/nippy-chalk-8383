package com.Dao.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.Dao.Utility.DBUtil;
import com.Dao.models.Bidder;

public class BidderDaoImpl implements BidderDao {

	@Override
	public String acceptBid(int tenderId) {
		
		String status = "Bid Assignment Failed";

		try(Connection con=DBUtil.provideConnection()) {

			PreparedStatement ps = con.prepareStatement("select * from tender where tid=? AND tstatus='Assigned'");
			ps.setInt(1, tenderId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				status = "Tender Already Assigned";
			} else {
				
				Bidder bid = bestBids(tenderId);
				
				if(bid==null) {
					status = "No Bids for the Tendor is Found";
				}else {
					PreparedStatement pst = con.prepareStatement("update bidder set status = ? where bid=? and status=?");

					pst.setString(1, "Accepted");
					pst.setInt(2, bid.getBid());
					pst.setString(3, "Pending");

					int x = pst.executeUpdate();
					if (x > 0) {
						status = "Bid Has Been Accepted Successfully!";
						TenderDao dao = new TenderDaoImpl();
						status = status + "\n" + dao.assignTender(tenderId);
					}
				}
				
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public String rejectBid(int tenderId) {
		
		String status = "Bid Rejection Failed";

		try (Connection con=DBUtil.provideConnection()) {
			PreparedStatement ps = con.prepareStatement("update bidder set status = ? where tid=? and status = ?");

			ps.setString(1, "Rejected");
			ps.setInt(2, tenderId);
			ps.setString(3, "Pending");

			int x = ps.executeUpdate();
			if (x > 0)
				status = "Bid Has Been Rejected Successfully!";

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return status;

	}

	@Override
	public String bidTender(Bidder bidder) {

		String status = "Tender Bidding Failed!";

		try (Connection con=DBUtil.provideConnection()) {
			PreparedStatement ps = con.prepareStatement("insert into bidder(vid,tid,bidAmount,status,biddate) values(?,?,?,?,sysdate())");
			ps.setString(1, bidder.getVid());
			ps.setInt(2, bidder.getTid());
			ps.setInt(3, bidder.getBidAmount());
			ps.setString(4, bidder.getStatus());

			int x = ps.executeUpdate();

			if (x > 0)
				status = "You have successfully Bid for the tender";

		} catch (SQLException e) {
			status = status + " Duplicate Bid Found or Invalid Tender Details Found";
		} 


		return status;
	}

	@Override
	public List<Bidder> getAllBidsOfaTender(int tenderId) {

		List<Bidder> bidderList = new ArrayList<Bidder>();

		try (Connection con=DBUtil.provideConnection()){
			PreparedStatement ps = con.prepareStatement("select * from bidder where tid=?");
			ps.setInt(1, tenderId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Bidder bidder = new Bidder();

				bidder.setBidAmount(rs.getInt("bidamount"));
				bidder.setBid(rs.getInt("bid"));
				bidder.setStatus(rs.getString("status"));
				bidder.setTid(rs.getInt("tid"));
				bidder.setVid(rs.getString("vid"));

				bidderList.add(bidder);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return bidderList;
	}

	@Override
	public List<Bidder> getAllBidsOfaVendor(String vendorId) {
		List<Bidder> bidderList = new ArrayList<Bidder>();

		try (Connection con=DBUtil.provideConnection()) {
//			PreparedStatement ps = null;
//			ResultSet rs = null;

			PreparedStatement ps = con.prepareStatement("select * from bidder where vid=?");
			ps.setString(1, vendorId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Bidder bidder = new Bidder();
				
				bidder.setBid(rs.getInt("bid"));
				bidder.setVid(rs.getString("vid"));
				bidder.setTid(rs.getInt("tid"));
				bidder.setBidAmount(rs.getInt("bidamount"));
				bidder.setStatus(rs.getString("status"));				

				bidderList.add(bidder);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return bidderList;
	}

	@Override
	public String getStatusOfABid(int tid,String vendorId) {

		String status = "Bid Not Found";

		try (Connection con=DBUtil.provideConnection()){
//			PreparedStatement ps = null;
//			ResultSet rs = null;

			PreparedStatement ps = con.prepareStatement("select * from bidder where tid=? AND vid=?");

			ps.setInt(1, tid);
			ps.setString(2, vendorId);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				status = rs.getString("status");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} 

		return status;

	}

	@Override
	public Bidder bestBids(int tendorId) {
		
		Bidder bidder = null;

		try (Connection con=DBUtil.provideConnection()){

			PreparedStatement ps = con.prepareStatement("select * from bidder where tid=? AND bidAmount = "
					+ "(select min(bidAmount) from bidder where tid=?) AND biddate = "
					+ "(select min(biddate) from bidder where tid=?)");

			ps.setInt(1, tendorId);
			ps.setInt(2, tendorId);
			ps.setInt(3, tendorId);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				bidder = new Bidder();
				
				bidder.setBid(rs.getInt("bid"));
				bidder.setVid(rs.getString("vid"));
				bidder.setTid(rs.getInt("tid"));
				bidder.setBidAmount(rs.getInt("bidamount"));
				bidder.setStatus(rs.getString("status"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} 

		
		return bidder;
	}

}
