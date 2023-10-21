package edu.miu.waa.onlineauctionapi.repository;

import edu.miu.waa.onlineauctionapi.model.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
  Page<Product> findByStatusOrderByIdAsc(String status, Pageable pageable);

  Page<Product> findByStatusAndNameContainsOrderByIdAsc(
      String status, String name, Pageable pageable);

  List<Product> findByOwner(String owner);

  Optional<List<Product>> findByStatus(String status);
}
