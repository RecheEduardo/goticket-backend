package tech.goticket.backendapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.entities.Organizer;
import tech.goticket.backendapi.repository.OrganizerRepository;

import java.util.InputMismatchException;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizerService {

    @Autowired
    private OrganizerRepository organizerRepository;

    public void saveOrganizer(Organizer organizer) { organizerRepository.save(organizer); }

    public Optional<Organizer> findByCNPJ(String CNPJ) {
        return this.organizerRepository.findByCNPJ(CNPJ);
    }

    public Optional<Organizer> findById(UUID organizerId) { return this.organizerRepository.findByUserID(organizerId); }

    public static boolean isCNPJ(String CNPJ) {
        if (CNPJ.equals("00000000000000") || CNPJ.equals("11111111111111") ||
            CNPJ.equals("22222222222222") || CNPJ.equals("33333333333333") ||
            CNPJ.equals("44444444444444") || CNPJ.equals("55555555555555") ||
            CNPJ.equals("66666666666666") || CNPJ.equals("77777777777777") ||
            CNPJ.equals("88888888888888") || CNPJ.equals("99999999999999") ||
            (CNPJ.length() != 14)){

            return(false);
        }

        char dig13, dig14;
        int sm, i, r, num, peso;

        // "try" - protege o código para eventuais erros de conversao de tipo (int)
        try {
            sm = 0;
            peso = 2;
            for (i=11; i>=0; i--) {
                num = (int)(CNPJ.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso + 1;
                if (peso == 10) peso = 2;
            }

            r = sm % 11;
            if ((r == 0) || (r == 1)) dig13 = '0';
            else dig13 = (char)((11-r) + 48);

            sm = 0;
            peso = 2;
            for (i=12; i>=0; i--) {
                num = (int)(CNPJ.charAt(i)- 48);
                sm = sm + (num * peso);
                peso = peso + 1;
                if (peso == 10) peso = 2;
            }

            r = sm % 11;
            if ((r == 0) || (r == 1)) dig14 = '0';
            else dig14 = (char)((11-r) + 48);

            if ((dig13 == CNPJ.charAt(12)) && (dig14 == CNPJ.charAt(13))) return(true);
            else return(false);
        } catch (InputMismatchException erro) {
            return(false);
        }
    }
}
