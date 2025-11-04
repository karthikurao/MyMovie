import React, { useEffect, useState } from 'react';
import { Navbar, Nav, Container, Button, Badge } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import axios from 'axios';

function Navigation() {
	const [user, setUser] = useState(null);
	const [isScrolled, setIsScrolled] = useState(false);

	useEffect(() => {
		const loadUserData = () => {
			try {
				const storedUser = localStorage.getItem('user');
				if (storedUser) {
					const userData = JSON.parse(storedUser);
					setUser(userData);
				} else {
					setUser(null);
				}
			} catch (error) {
				console.error('Error loading user data:', error);
				setUser(null);
			}
		};

		// Initial load
		loadUserData();

		// Listen for storage changes (when user logs in/out in another tab)
		const handleStorageChange = (e) => {
			if (e.key === 'user') {
				loadUserData();
			}
		};

		window.addEventListener('storage', handleStorageChange);

		// Also listen for custom user update events
		const handleUserUpdate = () => {
			loadUserData();
		};

		window.addEventListener('userUpdate', handleUserUpdate);

		const handleSessionExpired = (event) => {
			const detail = event?.detail;
			const message = typeof detail === 'string' ? detail : detail?.message;
			window.alert(message || 'Your session has expired. Please sign in again.');
		};

		window.addEventListener('sessionExpired', handleSessionExpired);

		const handleScroll = () => {
			setIsScrolled(window.scrollY > 50);
		};

		window.addEventListener('scroll', handleScroll, { passive: true });

		return () => {
			window.removeEventListener('storage', handleStorageChange);
			window.removeEventListener('userUpdate', handleUserUpdate);
			window.removeEventListener('sessionExpired', handleSessionExpired);
			window.removeEventListener('scroll', handleScroll);
		};
	}, []);

	const handleLogout = async () => {
		const payload = {
			email: user?.email,
			userId: user?.userId,
			role: user?.role,
		};

		try {
			if (payload.email) {
				await axios.post('/api/users/signout', payload, {
					headers: { 'Content-Type': 'application/json' },
				});
			}
		} catch (logoutError) {
			console.warn('Server sign-out failed', logoutError);
		} finally {
			localStorage.removeItem('user');
			setUser(null);
			window.location.href = '/';
		}
	};

	const navbarStyle = {
		boxShadow: isScrolled ? '0 12px 30px rgba(0, 0, 0, 0.15)' : 'var(--shadow-soft)',
		borderBottom: isScrolled
			? '1px solid rgba(102, 126, 234, 0.25)'
			: '1px solid rgba(255, 255, 255, 0.2)',
	};

	const getUserDisplayName = () => {
		if (!user) return 'Guest';
		return user.name || user.firstName || user.username || user.email || 'User';
	};

	const getUserRole = () => {
		if (!user || !user.role) return 'Guest';
		return user.role;
	};

	return (
		<Navbar
			className="navbar-modern"
			variant="light"
			expand="lg"
			fixed="top"
			style={navbarStyle}
		>
			<Container>
				<LinkContainer to="/">
					<Navbar.Brand className="d-flex align-items-center gap-2">
						<span style={{ fontSize: '1.75rem' }}>🎬</span>
						CinemaHub
					</Navbar.Brand>
				</LinkContainer>

				<Navbar.Toggle aria-controls="main-navbar" />

				<Navbar.Collapse id="main-navbar">
					<Nav className="ms-auto align-items-center gap-2">
						<LinkContainer to="/">
							<Nav.Link>Home</Nav.Link>
						</LinkContainer>

						<LinkContainer to="/movies">
							<Nav.Link>Movies</Nav.Link>
						</LinkContainer>

						<LinkContainer to="/shows">
							<Nav.Link>Shows</Nav.Link>
						</LinkContainer>

						<LinkContainer to="/theatres">
							<Nav.Link>Theatres</Nav.Link>
						</LinkContainer>

						{user && user.userId ? (
							<>
								{user.role === 'ADMIN' && (
									<LinkContainer to="/admin-dashboard">
										<Nav.Link>Admin</Nav.Link>
									</LinkContainer>
								)}

								{user.role === 'CUSTOMER' && (
									<>
										<LinkContainer to="/customer-dashboard">
											<Nav.Link>Dashboard</Nav.Link>
										</LinkContainer>
										<LinkContainer to="/tickets">
											<Nav.Link>My Tickets</Nav.Link>
										</LinkContainer>
									</>
								)}

								<div
									className="d-flex align-items-center gap-2 px-3 py-2 rounded-pill"
									style={{
										background: 'linear-gradient(135deg, rgba(102, 126, 234, 0.12), rgba(118, 75, 162, 0.12))',
										border: '1px solid rgba(102, 126, 234, 0.2)',
									}}
								>
									<span style={{ fontSize: '1.25rem' }}>👋</span>
									<div className="d-flex flex-column">
										<span className="fw-semibold" style={{ fontSize: '0.9rem' }}>
											{getUserDisplayName()}
										</span>
										{user && user.email && (
											<span className="text-muted" style={{ fontSize: '0.75rem' }}>
												{user.email}
											</span>
										)}
									</div>
									<Badge bg="light" text="dark" className="text-uppercase" style={{ fontSize: '0.65rem' }}>
										{getUserRole()}
									</Badge>
								</div>

								<Button
									variant="outline-danger"
									size="sm"
									className="btn-modern"
									onClick={handleLogout}
								>
									<span className="me-1">🚪</span>
									Logout
								</Button>
							</>
						) : (
							<div className="d-flex align-items-center gap-2">
								<LinkContainer to="/login">
									<Button variant="outline-primary" size="sm" className="btn-modern">
										<span className="me-1">🔐</span>
										Login
									</Button>
								</LinkContainer>

								<LinkContainer to="/register">
									<Button size="sm" className="btn-modern btn-primary-modern">
										<span className="me-1">✨</span>
										Register
									</Button>
								</LinkContainer>
							</div>
						)}
					</Nav>
				</Navbar.Collapse>
			</Container>
		</Navbar>
	);
}

export default Navigation;

