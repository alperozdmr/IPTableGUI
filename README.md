# Linux Iptables GUI Manager


## 🔥 Hakkında

**Linux Iptables GUI Manager**, Java ile yazılmış, `iptables` kurallarını grafiksel arayüz üzerinden kolayca yönetmenizi sağlayan bir uygulamadır. Komut satırı bilgisine gerek kalmadan IP, port, TCP bayrakları gibi filtreleme seçenekleriyle güvenlik duvarı kuralları oluşturabilir, listeleyebilir ve silebilirsiniz.

## 🎯 Özellikler

- TCP/UDP protokol seçimi
- TCP bayrakları (SYN, ACK, FIN, RST, PSH, URG) ile trafik filtreleme
- Kaynak ve hedef IP/port filtreleme
- `ACCEPT` veya `DROP` işlemleri
- INSERT veya APPEND modlarıyla kural ekleme
- Whitelist/Blacklist IP listeleri
- Kuralları listeleme, silme veya belirli bir satırı silme
- Basit kullanıcı arayüzü (Swing)

## 🚀 Kurulum ve Çalıştırma

### Gereksinimler

- Java JDK 8 veya üzeri
- Linux (iptables komutunun çalışması için root erişimi gereklidir)

### IDE Üzerinden Çalıştırma

1. Java IDE'nizde (örneğin IntelliJ IDEA veya Eclipse) bu projeyi açın.
2. `Main.java` dosyasını çalıştırın.
3. Uygulama başlatıldığında sizden `sudo` şifreniz istenir. Bu şifre `iptables` komutlarını çalıştırmak için kullanılır.

> Not: Uygulama içinden dış kabuk komutlarını çalıştırdığı için, çalıştığınız sistemde `iptables` komutunun mevcut ve erişilebilir olması gerekir.

## 📷 Ekran Görüntüsü

![GUI](./screenshot.png)

## 🛠 Kullanım

### Örnek 1: HTTP Trafiğini Kabul Et

- **Protocol**: TCP  
- **Destination Port**: 80  
- **Action**: ACCEPT  
- Ardından **Add Rule** butonuna tıklayın.

### Örnek 2: IP'yi Kara Listeye Almak

- **Blacklist IPs** alanına IP'yi yazın (örn: `192.168.1.100`)
- **Add Rule** butonuna tıklayın.

### Kuralları Görüntüleme

- **Refresh** butonuna tıklayarak aktif kuralları aşağıdaki metin kutusunda görebilirsiniz.

## ⚠️ Uyarı

- Yanlış yapılandırılmış iptables kuralları sisteminizin ağ bağlantısını kesebilir.
- Lütfen kurallarınızı dikkatle test edin.
- `sudo` şifreniz yalnızca yerel oturumda geçici olarak kullanılır.

## 🧩 Katkı Sağlama

Katkılar memnuniyetle karşılanır! Fork'layabilir, yeni özellikler ekleyip pull request gönderebilirsiniz.

## 📄 Lisans

Bu proje MIT Lisansı ile lisanslanmıştır. Daha fazla bilgi için `LICENSE` dosyasına göz atın.
