package com.rationportal.service;

import com.rationportal.model.RationCard;
import com.rationportal.repository.RationCardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RationCardImportServiceTests {
    private final RationCardRepository repository = mock(RationCardRepository.class);
    private final RationCardImportService service = new RationCardImportService(repository, 1024 * 1024);

    @Test
    void importsQuotedFieldsAndNormalizesOwnerData() {
        when(repository.existsByRationCardNumber("123456789012")).thenReturn(false);
        String csv = "ration_card_number,card_type,head_of_family,address,district,state,status,owner_email,mobile,pincode\n"
                + "123456789012,BPL,\"Rao, Rithwik\",\"1 Main Street, Block A\",Warangal,Telangana,ACTIVE,TEST@example.com,9876543210,506001\n";
        var file = file("cards.csv", csv);

        int count = service.importCsv(file);

        assertEquals(1, count);
        verify(repository).saveAll(org.mockito.ArgumentMatchers.<Iterable<RationCard>>argThat(cards -> {
            var iterator = cards.iterator();
            if (!iterator.hasNext()) return false;
            RationCard card = iterator.next();
            return card.getHeadOfFamily().equals("Rao, Rithwik")
                    && card.getAddress().equals("1 Main Street, Block A")
                    && card.getOwnerEmail().equals("test@example.com")
                    && card.getStatus().equals("ACTIVE")
                    && card.getApprovalStatus().equals("ACTIVE")
                    && card.getPincode().equals("506001");
        }));
    }

    @Test
    void invalidRowAbortsImportWithoutSavingAnyCards() {
        String csv = "ration_card_number,card_type,head_of_family,address,district,state\n"
                + "not-a-number,BPL,Test,Address,Warangal,Telangana\n";
        assertThrows(IllegalArgumentException.class, () -> service.importCsv(file("bad.csv", csv)));
        verify(repository, never()).saveAll(anyList());
    }

    @Test
    void duplicateCardNumberInSameFileIsRejected() {
        when(repository.existsByRationCardNumber("123456789012")).thenReturn(false);
        String csv = "ration_card_number,card_type,head_of_family,address,district,state\n"
                + "123456789012,BPL,Test One,Address,Warangal,Telangana\n"
                + "123456789012,BPL,Test Two,Address,Warangal,Telangana\n";
        assertThrows(IllegalArgumentException.class, () -> service.importCsv(file("duplicate.csv", csv)));
        verify(repository, never()).saveAll(anyList());
    }

    @Test
    void rejectsUnknownColumnsAndNonCsvFiles() {
        assertThrows(IllegalArgumentException.class, () ->
                service.importCsv(file("cards.txt", "ration_card_number,card_type,head_of_family,address,district,state\n")));
        assertThrows(IllegalArgumentException.class, () ->
                service.importCsv(file("cards.csv", "unexpected,card_type,head_of_family,address,district,state\n")));
        verify(repository, never()).saveAll(anyList());
    }

    @Test
    void defaultsMissingStatusToPendingAndRejectsUnsupportedStatus() {
        when(repository.existsByRationCardNumber("123456789012")).thenReturn(false);
        String valid = "ration_card_number,card_type,head_of_family,address,district,state\n"
                + "123456789012,BPL,Test,Address,Warangal,Telangana\n";
        assertEquals(1, service.importCsv(file("cards.csv", valid)));
        verify(repository).saveAll(org.mockito.ArgumentMatchers.<Iterable<RationCard>>argThat(cards ->
                cards.iterator().next().getStatus().equals("PENDING")));

        String invalid = "ration_card_number,card_type,head_of_family,address,district,state,status\n"
                + "123456789013,BPL,Test,Address,Warangal,Telangana,SUPERADMIN\n";
        assertThrows(IllegalArgumentException.class, () -> service.importCsv(file("cards.csv", invalid)));
    }

    private MockMultipartFile file(String filename, String content) {
        return new MockMultipartFile("file", filename, "text/csv", content.getBytes(StandardCharsets.UTF_8));
    }
}