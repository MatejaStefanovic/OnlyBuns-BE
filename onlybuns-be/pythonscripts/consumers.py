import pika
import json
import time
from pathlib import Path

def save_to_json_file(message, instance_id):
    directory = f"instance_{instance_id}_messages"
    Path(directory).mkdir(parents=True, exist_ok=True)  # Kreira folder za instancu
    timestamp = int(time.time())
    file_path = f"{directory}/message_{timestamp}.json"

    with open(file_path, "w") as json_file:
        json.dump(message, json_file, indent=4)
    print(f"Poruka sačuvana u: {file_path}")

def start_instance(instance_id):
    connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
    channel = connection.channel()

    # Kreira red i povezuje ga na fanout exchange
    channel.exchange_declare(exchange='advertisement_fanout_exchange', exchange_type='fanout', durable=True)
    queue = channel.queue_declare(queue='', exclusive=True)  # Kreira privremeni red
    queue_name = queue.method.queue

    

    channel.queue_bind(exchange='advertisement_fanout_exchange', queue=queue_name)

    print(f"Instanca {instance_id} čeka poruke...")

    def callback(ch, method, properties, body):
        message = json.loads(body)
        print(f"Instanca {instance_id} primila poruku: {message}")
        save_to_json_file(message, instance_id)

    channel.basic_consume(queue=queue_name, on_message_callback=callback, auto_ack=True)
    channel.start_consuming()

if __name__ == "__main__":
    import sys
    instance_id = sys.argv[1] if len(sys.argv) > 1 else "default"
    start_instance(instance_id)
