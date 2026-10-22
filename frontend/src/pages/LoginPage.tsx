import React, { useState, useEffect } from 'react';
import { Form, Button } from 'react-bootstrap';
import { Input } from '../components/Input.tsx'; // Adjust the import path based on your file structure
import { Redirect } from '../components/Redirect.tsx'
import type { User } from '../models/models'
import { type LoginError, falseLogin } from '../utils/validateLogin.ts'
import { useNavigate } from 'react-router-dom';
import { useToast } from '../components/ToastProvider';
import { login, setToken } from '../hooks/authService.tsx';

// to be replaced with actual login content
export function LoginPage() {

  return (
    <div className="container py-5">
      <div className="content-panel auth-panel">
      <h1>Welcome Back to Reactive Banking</h1>

      {/* Basic login form using the Input framework */}
      <LoginForm />

      {/* Testing displaying the authentication card
      <AuthCard 
        firstName="Bob"
        lastName="Smith"
        email="bobsmith@gmail.com"
        phoneNumber="123-456-7890"
        username="bobsmith"
        password="ilovemyself"
        password2="ilovemyself"
      />
      */}

      <hr />
      </div>
    </div>
  );
}

function LoginForm() {
  const navigate = useNavigate();
  const showToast = useToast();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const [user, setUser] = useState<User | undefined>(undefined);
  const [loginErrors, setLoginErrors] = useState<LoginError | undefined>(undefined);
  const [error, setError] = useState<string | null>(null);

  /*
  useEffect(() => {
    let active = true;

    async function loadAccountInfo() {
      try {
        const response = await UserService.logIn(1)

        if (!active) return

        if (response.error || !response.data) {
          setError(response.error ?? 'Unable to load account information')
        } else {
          setUser(response.data)
        }
      } catch {
        if (active) {
          setError('Unable to load user. Please try again')
        }
      }
    }

    loadAccountInfo();

    return () => {
      active = false;
    };
  }, []);
  */

  const handleSubmit = async (e : React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const currentUsername = username.trim();
    const currentPassword = password.trim();

    const freshErrors: LoginError = {
      Username : currentUsername === "" ? "Username is not present" : "",
      Password : currentPassword === "" ? "Password is not present" : ""
    }

    setLoginErrors(freshErrors);

    if (falseLogin(freshErrors)) {
      setError("Please fill out all required fields.");
      return; 
    }
    
    try {
      const token = await login({"Username" : username, "Password" : password});
      setToken(token);
      showToast('success', 'Login success!');
      navigate('/dashboard');
    } catch {
      showToast('danger', 'Authentication failed');
    }
  }

  return (
    <Form onSubmit={handleSubmit} className="p-4 border rounded">
      <Input 
        label="Username" 
        name="username" 
        type="text" 
        placeholder="jdoe123"
        value={username}
        onChange={(e) => {
          setUsername(e.target.value);
        }}
        error={loginErrors && loginErrors.Username.length > 0 ? loginErrors.Username : ''}
      />

      {/* Email Input with Error Handling */}
      <Input 
        label="Password" 
        name="password" 
        type="password" 
        placeholder="Password"
        value={password}
        onChange={(e) => {
          setPassword(e.target.value);
        }}
        error={loginErrors && loginErrors.Password.length > 0 ? loginErrors.Password : ''}
      />

      <Redirect
        style={{display: "inline-block", padding: "0px 0px 10px"}}
        link="/register"
        message="Don't have an account? Click here to register"
      />

      <Button type="submit" variant="primary">Submit</Button>
    </Form>
  );
}
