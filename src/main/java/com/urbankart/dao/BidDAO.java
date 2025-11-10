package com.urbankart.dao;

import com.urbankart.model.Bid;
import java.util.List;

public interface BidDAO {
    boolean placeBid(Bid bid);
    Bid getHighestBid(int productId);
    List<Bid> getBidsByProduct(int productId);
    List<Bid> getBidsByUser(int userId);
    boolean updateWinningBid(int productId, int winningBidId);
    Bid getBidById(int bidId);
    // Remove the updateProductCurrentBid method from the interface
}