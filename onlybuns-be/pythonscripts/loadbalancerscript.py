import requests
import time

    # --- Podesi ove vrednosti ---
    # URL tvog Round Robin Load Balancera
    # OVO JE KLJUČNO: Zameni sa stvarnom adresom tvog load balancera i endpointa!
    # U tvom slucaju, ovo je adresa LoadBalancerController-a
load_balancer_url = "http://localhost:8080/call-backend-manual-lb" # <--- PRILAGODI PORT (8000) AKO JE DRUGACIJI!

num_requests = 15 # Broj zahteva koje ćeš poslati
delay_seconds = 0.1 # Kratka pauza između zahteva (da ne preopteretiš odmah servere)
    # ---------------------------

print(f"Slanje {num_requests} zahteva na load balancer: {load_balancer_url}")
print("-" * 50)

for i in range(1, num_requests + 1):
    try:
        response = requests.get(load_balancer_url)
            # Pokusaj da procitas JSON odgovor ako je API vratio JSON
        try:
            response_data = response.json()
            response_text_preview = str(response_data)[:100]
        except ValueError:
                # Ako nije JSON, uzmi samo tekst
            response_text_preview = response.text[:100]

        print(f"Zahtev {i}: Status {response.status_code}, Odgovor: {response_text_preview}...")
    except requests.exceptions.ConnectionError as e:
        print(f"Zahtev {i}: GREŠKA KONEKCIJE - Proveri da li je load balancer pokrenut i dostupan. Detalji: {e}")
    except Exception as e:
        print(f"Zahtev {i}: NEOČEKIVANA GREŠKA - {e}")

    time.sleep(delay_seconds)

print("-" * 50)
print("Testiranje završeno.")
