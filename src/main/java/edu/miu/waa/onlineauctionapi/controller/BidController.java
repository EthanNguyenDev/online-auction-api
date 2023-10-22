package edu.miu.waa.onlineauctionapi.controller;

import edu.miu.waa.onlineauctionapi.common.Constants;
import edu.miu.waa.onlineauctionapi.dto.ApiResponse;
import edu.miu.waa.onlineauctionapi.dto.BidResponse;
import edu.miu.waa.onlineauctionapi.exception.BidProcessingException;
import edu.miu.waa.onlineauctionapi.model.Bid;
import edu.miu.waa.onlineauctionapi.model.User;
import edu.miu.waa.onlineauctionapi.service.BidService;
import edu.miu.waa.onlineauctionapi.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(Constants.BIDS_URL_PREFIX)
public class BidController {
  private final BidService bidService;
  private final UserService userService;

  @PostMapping
  public BidResponse addBid(@RequestBody Bid bid) {
    String email = bid.getUser().getEmail();

    // check valid user
    User user = userService.findUser(email);
    if (user == null) {
      return BidResponse.builder().success(false).message("Invalid user").build();
    }
    bid.setUser(user);

    // check if deposit
    boolean hasDeposit = bidService.hasDeposit(user.getId(), bid.getProduct().getId());
    if (!hasDeposit) {
      return BidResponse.builder()
          .success(false)
          .message("Required deposit")
          .requiredDeposit(true)
          .build();
    }
    // save bid
    bidService.addBid(bid);
    return BidResponse.builder().success(true).build();
  }

  @PostMapping("/deposit")
  public BidResponse makeDeposit(@RequestBody Bid bid) {
    return bidService.makeDeposit(bid);
  }

  @GetMapping("/my-history")
  public List<Bid> getMyBidHistory() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String userId = authentication.getName();

    var history = bidService.findByUserIdOrderByProductIdAscBidDateDesc(userId);

    return history;
  }

  @PostMapping("/settle/{productId}")
  public ApiResponse<?> settleProductBids(@PathVariable long productId)
      throws BidProcessingException {
    bidService.settleProductBidsById(productId);
    return ApiResponse.builder().success(true).build();
  }
}
