import React, { useEffect, useState } from 'react';
import { Navbar, Nav, Container, Button, Badge } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';

function Navigation() {
	const [user, setUser] = useState(null);
	const [isScrolled, setIsScrolled] = useState(false);

	useEffect(() => {
		try {
			const storedUser = localStorage.getItem('user');
			if (storedUser) {
				setUser(JSON.parse(storedUser));
			}
		} catch (error) {
			setUser(null);
		}

		const handleScroll = () => {
			setIsScrolled(window.scrollY > 50);
		};

		window.addEventListener('scroll', handleScroll, { passive: true });
		return () => window.removeEventListener('scroll', handleScroll);
	}, []);

	const handleLogout = () => {
		localStorage.removeItem('user');
		window.location.href = '/';
	};

	const navbarStyle = {
		boxShadow: isScrolled ? '0 12px 30px rgba(0, 0, 0, 0.15)' : 'var(--shadow-soft)',
		borderBottom: isScrolled
			? '1px solid rgba(102, 126, 234, 0.25)'
			: '1px solid rgba(255, 255, 255, 0.2)',
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
									<LinkContainer to="/admin">
										<Nav.Link>Admin</Nav.Link>
									</LinkContainer>
								)}

								{user.role === 'CUSTOMER' && (
									<LinkContainer to="/customer">
										<Nav.Link>Dashboard</Nav.Link>
									</LinkContainer>
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
											{user.firstName || user.username || 'Guest'}
										</span>
										{user.email && (
											<span className="text-muted" style={{ fontSize: '0.75rem' }}>
												{user.email}
											</span>
										)}
									</div>
									{user.role && (
										<Badge bg="light" text="dark" className="text-uppercase" style={{ fontSize: '0.65rem' }}>
											{user.role}
										</Badge>
									)}
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

