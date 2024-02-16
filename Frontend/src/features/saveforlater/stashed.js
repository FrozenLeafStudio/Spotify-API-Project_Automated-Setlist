/*
  ------------NOTE: Admin functions/states--------------
  const [isAdminModalOpen, setIsAdminModalOpen] = useState<boolean>(false);
 const [authUrl, setAuthUrl] = useState<string>("");
  useEffect(() => {
    const keySequence: string[] = ["Control", "Alt", "Shift", "A"];
    let keyPressed: string[] = [];

    const keyDownHandler = (event: KeyboardEvent) => {
      keyPressed.push(event.key);
      if (keyPressed.length > keySequence.length) {
        keyPressed.shift();
      }

      if (keySequence.every((key, index) => key === keyPressed[index])) {
        setIsAdminModalOpen(true);
        keyPressed = [];
      }
    };

    window.addEventListener("keydown", keyDownHandler);

    return () => window.removeEventListener("keydown", keyDownHandler);
  }, []);

  const handleAdminSubmit = async (username: string, password: string) => {
    try {
      const authData = await initiateAuthorization(username, password);
      setAuthUrl(authData.url);
    } catch (error) {
      console.error("Error during Spotify authorization: ", error);
    }
  };

  const closeModal = () => {
    setIsAdminModalOpen(false);
    setAuthUrl("");
  };

  JSX to be added before fragment close:
  <Modal isOpen={isAdminModalOpen} onClose={closeModal}>
        <AdminLogin onAdminSubmit={handleAdminSubmit} />
        {authUrl && (
          <div>
            <p>
              Please go to this URL to authorize:{" "}
              <a href={authUrl} target="_blank" rel="noopener noreferrer">
                {authUrl}
              </a>
            </p>
            <button onClick={closeModal}>Close</button>
          </div>
        )}
      </Modal>
  */
