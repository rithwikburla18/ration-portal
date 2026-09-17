package com.rationportal.service; import com.rationportal.model.RationCard; import com.rationportal.repository.RationCardRepository; import org.springframework.stereotype.Service; import org.springframework.lang.NonNull; import java.util.List; @Service public class RationCardService { private final RationCardRepository repository; public RationCardService(RationCardRepository repository){this.repository=repository;} public List<RationCard> getAll(){return repository.findAll();} public RationCard getByNumber(String number){return repository.findByRationCardNumber(number).orElse(null);} public RationCard save(@NonNull RationCard card){return repository.save(card);}  }




