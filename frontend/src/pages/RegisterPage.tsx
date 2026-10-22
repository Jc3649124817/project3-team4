import React, { useState } from 'react';
import { Redirect } from '../components/Redirect.tsx'
import { Input } from '../components/Input.tsx'
import { Alert, Form, Button } from 'react-bootstrap'
import { type RegisterError, falseRegister } from '../utils/validateRegister.ts'
import { useNavigate } from 'react-router-dom';
import { useToast } from '../components/ToastProvider';
import { register } from '../hooks/authService.tsx';

//to be replaced with actual register content
export function RegisterPage() {
  return (
    <div className="container py-5">
      <div className="content-panel auth-panel">
      <h1>Register for Reactive Banking</h1>

      {/* Basic Bootstrap form for testing purposes */} 
      <RegisterForm />
      </div>
      
    </div>
  );
}

function RegisterForm() {
  const navigate = useNavigate();
  const showToast = useToast();
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [username, setUsername] = useState('');
  const [password1, setPassword1] = useState('');
  const [password2, setPassword2] = useState('');

  const [registerErrors, setRegisterErrors] = useState<RegisterError | undefined>(undefined);
  const [error, setError] = useState('');

  const handleSubmit = async (e : React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();

    const currentFirstName = firstName.trim();
    const currentLastName = lastName.trim();
    const currentEmail = email.trim();
    const currentPhoneNumber = phoneNumber.trim();
    const currentUsername = username.trim();
    const currentPassword = password1;
    const currentPassword2 = password2;

    const freshErrors: RegisterError = {
      FirstName : currentFirstName === "" ? "First name is not present" : "",
      LastName : currentLastName === "" ? "Last name is not present" : "",
      Email : currentEmail === "" ? "Email is not present" : "",
      PhoneNumber : currentPhoneNumber === "" ? "Phone number is not present" : "",
      Username : currentUsername === "" ? "Username is not present" : "",
      Password : currentPassword === "" ? "Password is not present" : "",
      Password2 : currentPassword2 === "" ? "Password confirmation is not present" : ""
    }

    setRegisterErrors(freshErrors);
    
    if (falseRegister(freshErrors)) {
      setError("Please fill out all required fields.");
      return; 
    }

    setError('');
    
    try {
      await register({"FirstName" : firstName, "LastName" : lastName, "Email" : email, "PhoneNumber" : phoneNumber, "Username" : username, "Password" : password1, "Password2" : password2});
      showToast('success', 'Account created! Redirecting to login...');
      navigate('/login');
    } catch {
      showToast('danger', 'Registration failed');
    }
  }

  return (
    <Form onSubmit={handleSubmit} className="p-4 border rounded">
      {error && <Alert variant="danger">{error}</Alert>}
      <Input 
        label="First Name" 
        name="firstName" 
        type="text" 
        placeholder="John"
        value={firstName}
        onChange={(e) => setFirstName(e.target.value)}
        error={registerErrors && registerErrors.FirstName.length > 0 ? registerErrors.FirstName : ""} 
      />

      <Input 
        label="Last Name" 
        name="lastName" 
        type="text" 
        placeholder="Doe"
        value={lastName}
        onChange={(e) => setLastName(e.target.value)}
        error={registerErrors && registerErrors.LastName.length > 0 ? registerErrors.LastName : ""}  
      />

      <Input 
        label="Email" 
        name="email" 
        type="email" 
        placeholder="johndoe@gmail.com"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        error={registerErrors && registerErrors.Email.length > 0 ? registerErrors.Email : ""}  
      />

      <Input 
        label="Phone Number" 
        name="phoneNumber" 
        type="text" 
        placeholder="123-456-7890"
        value={phoneNumber}
        onChange={(e) => setPhoneNumber(e.target.value)}
        error={registerErrors && registerErrors.PhoneNumber.length > 0 ? registerErrors.PhoneNumber : ""} 
      />

      <Input 
        label="Username" 
        name="username" 
        type="text" 
        placeholder="jdoe123"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        error={registerErrors && registerErrors.Username.length > 0 ? registerErrors.Username : ""} 
      />

      <Input 
        label="Password" 
        name="password1" 
        type="password" 
        placeholder="password"
        value={password1}
        onChange={(e) => {
          setPassword1(e.target.value);
        }}
        error={registerErrors && registerErrors.Password.length > 0 ? registerErrors.Password : ""} 
      />

      <Input 
        label="Confirm Password" 
        name="password2" 
        type="password" 
        placeholder="password"
        value={password2}
        onChange={(e) => {
          setPassword2(e.target.value);
        }}
        error={registerErrors && registerErrors.Password2.length > 0 ? registerErrors.Password2 : ""} 
      />

      <Redirect
        style={{display: "inline-block", padding: "0px 0px 10px"}}
        link="/login"
        message="Already have an account? Click here to login"
      />

      <Button type="submit" variant="primary">Submit</Button>
    </Form>
  );
}

