package com.rationportal.service; import com.rationportal.model.FamilyMember; import com.rationportal.repository.FamilyMemberRepository; import org.springframework.stereotype.Service; import org.springframework.lang.NonNull; import java.util.List; @Service public class FamilyMemberService { private final FamilyMemberRepository repository; public FamilyMemberService(FamilyMemberRepository repository){this.repository=repository;} public List<FamilyMember> getByRationCard(Long rationCardId){return repository.findByRationCardId(rationCardId);} public FamilyMember save(@NonNull FamilyMember member){return repository.save(member);}  }




