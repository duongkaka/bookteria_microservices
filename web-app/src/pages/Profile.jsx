import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Alert,
  Avatar,
  Box,
  Button,
  Card,
  CircularProgress,
  Snackbar,
  TextField,
  Tooltip,
  Typography,
} from "@mui/material";
import { getMyInfo } from "../services/userService";
import { isAuthenticated } from "../services/authenticationService";
import Scene from "./Scene";
import { logOut } from "../services/authenticationService";
import dayjs from "dayjs";
import PhotoCameraIcon from "@mui/icons-material/PhotoCamera";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { updateProfile, uploadAvatar } from "../services/userService";

export default function Profile() {
  const navigate = useNavigate();
  // Form state
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [city, setCity] = useState(null);
  const [dob, setDob] = useState(null);
  // Snackbar state
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState("");
  const [snackbarSeverity, setSnackbarSeverity] = useState("success");
   // Loading state
  const [loading, setUploading] = useState(false);
  const fileInputRef = useRef(null);

  const [userDetails, setUserDetails] = useState({});

  const getUserDetails = async () => {
    try {
      const response = await getMyInfo();
      const data = response.data;
      console.log("data",data)

      setUserDetails(data.result);

      setFirstName(userDetails.firstName || "");
      setLastName(userDetails.lastName || "");
      setEmail(userDetails.email || "");
      setCity(userDetails.city || "");
      setDob(userDetails.dob ? dayjs(data.result.dob) : null);
    } catch (error) {
      if (error.response.status === 401) {
        logOut();
        navigate("/login");
      }
    }
  };

  const handleUpdate = async () => {
    // Prepare the data for update
    const profileData = {
      firstName,
      lastName,
      email,
      city,
      dob: dob ? dob.format("YYYY-MM-DD") : null,
    };
    console.log("data", profileData)
    await updateProfile(profileData);
    const updateDetails = {
      ...userDetails,
      ...profileData,
    };
    setUserDetails(updateDetails);
    // Show success message

    setSnackbarMessage("Profile updated successfully!");
    setSnackbarSeverity("success");
    setSnackbarOpen(true);
  };

  const handleAvatarClick = () => {
    fileInputRef.current.click();
  };

  const handleFileSelect = async (e) => {
    const file = e.target.files[0];

    if (!file) return;
    // Validate file type
    if (!file.type.match("image.*")) {
      setSnackbarMessage("写真選択してください。");
      setSnackbarSeverity("エラーになります。");
      setSnackbarOpen(true);
      return;
    }
    try {
      setUploading(true);
      //Create FormData object
      const formData = new FormData();
      formData.append("file", file);

      // Upload Image
      const response = await uploadAvatar(formData);

      //For demo purpose , create a local URL for image
      const imageUrl = response.data.result.avatar;
      console.log(imageUrl);

      // Update user details with the new avatar URL
      setUserDetails({
        ...userDetails,
        avatar: imageUrl,
      });

      console.log(userDetails.avatar);
      // Success message
      setSnackbarMessage("Avatar updated successfully!");
      setSnackbarSeverity("success");
      setSnackbarOpen(true);
    } catch (error) {
      console.error("Error uploading avatar:", error);
      setSnackbarMessage("Failed to upload avatar. Please try again.");
      setSnackbarSeverity("error");
      setSnackbarOpen(true);
    } finally {
      setUploading(false);
    }
  };

  const handleSnackbarClose = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }
    setSnackbarOpen(false);
  };

  useEffect(() => {
    if (!isAuthenticated()) {
      navigate("/login");
    } else {
      getUserDetails();
    }
  }, [navigate]);

  return (
    <Scene>
      <Snackbar
        open={snackbarOpen}
        autoHideDuration={6000}
        onClose={handleSnackbarClose}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
      >
        <Alert
          onClose={handleSnackbarClose}
          severity={snackbarSeverity}
          sx={{ width: "100%" }}
        >
          {snackbarMessage}
        </Alert>
      </Snackbar>

      {userDetails ? (
        <Card
          sx={{
            minWidth: 350,
            maxWidth: 500,
            boxShadow: 3,
            borderRadius: 2,
            padding: 4,
          }}
        >
          <Box
            sx={{
              display: "flex",
              flexDirection: "column",
              alignItems: "flex-start",
              width: "100%",
              gap: "10px",
            }}
          >
            <Typography
              sx={{
                fontSize: 18,
                mb: "40px",
              }}
            >
              Welcome back to Duong WEB, {userDetails.username} !
            </Typography>
            <Box
              sx={{
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                width: "100%",
                gap: "20px",
                mb: "30px",
              }}
            >
              <Tooltip title="Click to upload a profile picture">
                <Box sx={{ position: "relative" }}>
                  <Avatar
                    src={userDetails.avatar}
                    sx={{
                      width: 120,
                      height: 120,
                      fontSize: 48,
                      bgcolor: "#1976d2",
                      cursor: "pointer",
                      transition: "opacity 0.3s",
                      "&:hover": {
                        opacity: 0.8,
                      },
                    }}
                    onClick={handleAvatarClick}
                  ></Avatar>
                  <Box
                    sx={{
                      position: "absolute",
                      top: 0,
                      left: 0,
                      right: 0,
                      bottom: 0,
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      opacity: 0,
                      transition: "opacity 0.3s",
                      borderRadius: "50%",
                      backgroundColor: "rgba(0, 0, 0, 0.4)",
                      "&:hover": {
                        opacity: 1,
                      },
                      cursor: "pointer",
                    }}
                    onClick={handleAvatarClick}
                  >
                    <PhotoCameraIcon sx={{ color: "white", fontSize: 36 }} />
                  </Box>
                </Box>
              </Tooltip>
              <input
                type="file"
                accept="image/*"
                ref={fileInputRef}
                style={{ display: "none" }}
                onChange={handleFileSelect}
              />
            </Box>
            <Box
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                alignItems: "flex-start",
                width: "100%", // Ensure content takes full width
              }}
            >
              <Typography
                sx={{
                  fontSize: 14,
                  fontWeight: 600,
                }}
              >
                User Id
              </Typography>
              <Typography
                sx={{
                  fontSize: 14,
                }}
              >
                {userDetails.username}
              </Typography>
            </Box>
            <Box
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                alignItems: "center",
                width: "100%",
              }}
            >
              <Typography
                sx={{
                  fontSize: 14,
                  fontWeight: 600,
                }}
              >
                First Name
              </Typography>
              <TextField
                size="small"
                value={firstName || userDetails.firstName}
                onChange={(e) => setFirstName(e.target.value)}
                sx={{ width: "60%" }}
              />
            </Box>
            {/* Last Name */}
            <Box
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                alignItems: "center",
                width: "100%",
              }}
            >
              <Typography
                sx={{
                  fontSize: 14,
                  fontWeight: 600,
                }}
              >
                Last Name
              </Typography>
              <TextField
                size="small"
                value={lastName || userDetails.lastName}
                onChange={(e) => setLastName(e.target.value)}
                sx={{ width: "60%" }}
              />
            </Box>
            {/* Email */}
            <Box
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                alignItems: "center",
                width: "100%",
              }}
            >
              <Typography
                sx={{
                  fontSize: 14,
                  fontWeight: 600,
                }}
              >
                Email
              </Typography>
              <TextField
              
                size="small"
                value={email || userDetails.email}
                onChange={(e) => setEmail(e.target.value)}
                sx={{fontSize: 10, width: "60%" }}
                type="email"
              />
            </Box>

            {/* City */}
            <Box
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                alignItems: "center",
                width: "100%",
              }}
            >
              <Typography
                sx={{
                  fontSize: 14,
                  fontWeight: 600,
                }}
              >
                City
              </Typography>
              <TextField
                size="small"
                value={city || userDetails.city}
                onChange={(e) => setCity(e.target.value)}
                sx={{ width: "60%" }}
              />
            </Box>

            {/* Date of Birth */}
            <Box
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                alignItems: "flex-start",
                width: "100%", // Ensure content takes full width
              }}
            >
              <Typography
                sx={{
                  fontSize: 14,
                  fontWeight: 600,
                }}
              >
                Date of birth
              </Typography>
              <LocalizationProvider dateAdapter={AdapterDayjs}>
                <DatePicker
                  value={dob || (dayjs(userDetails.dob))}
                  onChange={(newValue) => setDob(newValue)}
                  slotProps={{ textField: { size: "small" } }}
                  sx={{ width: "60%" }}
                ></DatePicker>
              </LocalizationProvider>
            </Box>
            {/* Update Button */}
            <Box
              sx={{
                display: "flex",
                justifyContent: "center",
                width: "100%",
                mt: 3,
              }}
            >
              <Button
                sx={{ px: 4 }}
                onClick={handleUpdate}
                color="primary"
                variant="contained"
              >
                Update Profile
              </Button>
            </Box>
          </Box>
        </Card>
      ) : (
        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            gap: "30px",
            justifyContent: "center",
            alignItems: "center",
            height: "100vh",
          }}
        >
          <CircularProgress></CircularProgress>
          <Typography>Loading ...</Typography>
        </Box>
      )}
    </Scene>
  );
}
