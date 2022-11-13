package com.Dao.useCases;

import java.util.List;
import java.util.Scanner;

import com.Dao.dao.BidderDao;
import com.Dao.dao.BidderDaoImpl;
import com.Dao.dao.TenderDao;
import com.Dao.dao.TenderDaoImpl;
import com.Dao.dao.VendorDao;
import com.Dao.dao.VendorDaoImpl;
import com.Dao.models.Bidder;
import com.Dao.models.Tender;
import com.Dao.models.Vendor;

public class VendorUser extends User {

	public VendorUser() {
		super();
	}

	public VendorUser(String username, String password) {
		super(username, password);
	}

	public boolean loginVendor() {

		VendorDao vdao = new VendorDaoImpl();

		if (vdao.validatePassword(getUsername(), getPassword())) {

			System.out.println("============================================");
			System.out.println("Login Succesfull");
			System.out.println("============================================");
			return true;
		} else {
			System.out.println("============================================");
			System.out.println("Login Denied! Invalid User Details");
			System.out.println("============================================");
			return false;
		}
	}

	public void updateProfile() {

		Scanner sc = new Scanner(System.in);

		Vendor vendor = new Vendor(this.getUsername(), this.getPassword());

		System.out.println("Enter Vender Details:");
		System.out.println("Name:");
		vendor.setVname(sc.nextLine());

		System.out.println("Email:");
		vendor.setVemail(sc.nextLine());

		System.out.println("Company:");
		vendor.setCompany(sc.nextLine());

		System.out.println("Mobile:");
		vendor.setVmob(sc.nextLine());

		System.out.println("Address");
		vendor.setAddress(sc.nextLine());

		String status = new VendorDaoImpl().updateProfile(vendor);

		System.out.println("============================================");
		System.out.println(status);
		System.out.println("============================================");
	}

	public void viewAllCurrentTendors() {

		TenderDao tdao = new TenderDaoImpl();

		List<Tender> tenders = tdao.getAllTenders();

		if (tenders.isEmpty()) {
			System.out.println("============================================");
			System.out.println("No Tenders Found");
			System.out.println("============================================");
		} else {

			System.out.println("============================================");
			System.out.println("List of ALL Current Not Assigned Tenders: ");
			System.out.println("============================================");
			int count = 1;
			for (int i = 0; i < tenders.size(); i++) {
				Tender t = tenders.get(i);
				if (t.getTstatus().equalsIgnoreCase("Not Assigned")) {

					System.out.println(count + " Tender Details:");
					System.out.println("*ID: " + t.getTid());
					System.out.println("*Title: " + t.getTname());
					System.out.println("*Price: " + t.getTprice());
					System.out.println("*Type: " + t.getTtype());
					System.out.println("*Description: " + t.getTdesc());
					System.out.println("*Status: " + t.getTstatus());
					System.out.println("=================================");

					count++;
				}

			}
		}

	}

	public void bidTender() {

		Scanner sc = new Scanner(System.in);

		try {
			System.out.println("Enter Tender Id");
			int tid = Integer.parseInt(sc.nextLine());

			Tender t = new TenderDaoImpl().getTenderDataById(tid);

			if (t == null || t.getTstatus().equalsIgnoreCase("Assigned")) {

				System.out.println("============================================");
				System.out.println("Tender: " + tid + " not Found");
				System.out.println("============================================");
			} else {
				System.out.println("Enter Bid Amount");
				int bidAmount = Integer.parseInt(sc.nextLine());

				BidderDao bdao = new BidderDaoImpl();

				String status = bdao.bidTender(new Bidder(0, this.getUsername(), tid, bidAmount, "Pending"));

				System.out.println("============================================");
				System.out.println(status);
				System.out.println("============================================");
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid Input");
		}

	}

	public void getAllBidsOfaVendor() {

		BidderDao bdao = new BidderDaoImpl();

		List<Bidder> bids = bdao.getAllBidsOfaVendor(this.getUsername());

		if (bids.isEmpty()) {
			System.out.println("============================================");
			System.out.println("No Bids Found");
			System.out.println("============================================");
		} else {
			System.out.println("============================================");
			System.out.println("List of Bids For Vendor: " + this.getUsername());
			System.out.println("============================================");
			int count = 1;
			for (int i = 0; i < bids.size(); i++) {
				Bidder b = bids.get(i);
				System.out.println(count + " Bids Details:");
				System.out.println("*Bid ID: " + b.getBid());
				System.out.println("*Tender ID: " + b.getTid());
				System.out.println("*Vendor ID: " + b.getVid());
				System.out.println("*Bid Amount: " + b.getBidAmount());
				System.out.println("*Bid Status: " + b.getStatus());
				System.out.println("=================================");

				count++;
			}
		}
	}

	public void getStatusOfABid() {

		Scanner sc = new Scanner(System.in);

		try {
			System.out.println("Enter the Tender Id to get Its Bid Status");
			int tid = Integer.parseInt(sc.nextLine());
			BidderDao bdao = new BidderDaoImpl();

			String status = bdao.getStatusOfABid(tid, this.getUsername());

			System.out.println("============================================");
			System.out.println("Status for Bid on Tender id : " + tid + " is " + status);
			System.out.println("============================================");
		} catch (NumberFormatException e) {
			System.out.println("Invalid Input");
		}

	}

}
