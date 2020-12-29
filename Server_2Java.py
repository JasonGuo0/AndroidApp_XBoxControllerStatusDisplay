import socket

HOST = '0.0.0.0'  # Standard loopback interface address (localhost)
PORT = 2000		# Port to listen on (non-privileged ports are > 1023)

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
	try:
		s.bind((HOST, PORT)) #Associate the socket with a specific network interface and port number
		s.listen() #Enables the server to accept() connections. It starts the socket listening
		print("Python served built. Start listening")
		conn, addr = s.accept() #Blocks and waits for an incoming connection
		with conn:
			print('Connected by', addr)
			while True:
				data = conn.recv(1024) #A blocking call. It reads the data the client sends, and echoes it back using sendall()
				print("Data received", data)
				if not data or data == b"Over": #If the bytes object is empty, b'', then close the connection
					print("No data received or received 'Over'. Ending program")
					break
	except KeyboardInterrupt:
		pass