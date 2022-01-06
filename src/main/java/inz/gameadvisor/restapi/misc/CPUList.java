package inz.gameadvisor.restapi.misc;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CPUList {
    private String producent;
    private String seria;
    private String nazwa;
    private float wynik;

    public CPUList(String producent, String seria, String nazwa, float wynik) {
        this.producent = producent;
        this.seria = seria;
        this.nazwa = nazwa;
        this.wynik = wynik;
    }
}

