package inz.gameadvisor.restapi.misc;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GPUList {
    private String producent;
    private String seria;
    private String nazwa;
    private float wynik;

    public GPUList(String producent, String seria, String nazwa, float wynik) {
        this.producent = producent;
        this.seria = seria;
        this.nazwa = nazwa;
        this.wynik = wynik;
    }
}
