import React, { useEffect, useMemo, useState, useCallback } from 'react';
import { Container, Row, Col, Card, Badge, Button, Alert, Spinner, ButtonGroup, ToggleButton, Form } from 'react-bootstrap';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';

const FILTER_OPTIONS = [
    { key: 'all', label: 'All' },
    { key: 'upcoming', label: 'Upcoming' },
    { key: 'past', label: 'Past' }
];

function TicketView() {
    const [tickets, setTickets] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [activeFilter, setActiveFilter] = useState('all');
    const [searchTerm, setSearchTerm] = useState('');
    const [highlightId, setHighlightId] = useState(null);
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const storedUser = JSON.parse(localStorage.getItem('user') || '{}');
        if (!storedUser.userId || storedUser.role !== 'CUSTOMER') {
            navigate('/login');
            return;
        }

        const bookingToHighlight = location.state?.bookingId;
        if (bookingToHighlight) {
            setHighlightId(bookingToHighlight);
        }

        const fetchTickets = async () => {
            try {
                setLoading(true);
                setError('');
                const response = await axios.get(`/api/bookings/customer/${storedUser.userId}`, { timeout: 10000 });
                setTickets(Array.isArray(response.data) ? response.data : []);
            } catch (err) {
                console.error('Error fetching tickets:', err);
                if (err.code === 'ECONNABORTED') {
                    setError('Request timeout. Please verify the backend server status.');
                } else if (err.response && err.response.status === 404) {
                    setError('No bookings were found for this account.');
                    setTickets([]);
                } else {
                    setError('Unable to load your tickets right now. Please try again later.');
                }
            } finally {
                setLoading(false);
            }
        };

        fetchTickets();
    }, [navigate, location.state]);

    const clearHighlight = useCallback(() => {
        setHighlightId(null);
        if (location.state?.bookingId) {
            navigate(location.pathname, { replace: true });
        }
    }, [location.pathname, location.state, navigate]);

    const filteredTickets = useMemo(() => {
        const now = new Date();
        return tickets.filter((ticket) => {
            const showDate = ticket.showStartTime ? new Date(ticket.showStartTime) : null;
            const matchesFilter = (() => {
                if (activeFilter === 'upcoming') {
                    return showDate ? showDate >= now : false;
                }
                if (activeFilter === 'past') {
                    return showDate ? showDate < now : false;
                }
                return true;
            })();

            if (!matchesFilter) {
                return false;
            }

            if (!searchTerm.trim()) {
                return true;
            }

            const term = searchTerm.trim().toLowerCase();
            return [
                ticket.movieName,
                ticket.theatreName,
                ticket.showName,
                ticket.paymentReference,
                ticket.bookingReference ? ticket.bookingReference.toString() : ''
            ]
                .filter(Boolean)
                .some((field) => field.toLowerCase().includes(term));
        });
    }, [tickets, activeFilter, searchTerm]);

    const formatDateTime = (value) => {
        if (!value) {
            return 'Date TBD';
        }
        return new Date(value).toLocaleString('en-US', {
            weekday: 'short',
            month: 'short',
            day: 'numeric',
            hour: 'numeric',
            minute: '2-digit'
        });
    };

    const copyToClipboard = async (text) => {
        if (!text) {
            return;
        }
        try {
            await navigator.clipboard.writeText(text);
            window.alert('Copied to clipboard');
        } catch (copyError) {
            console.warn('Clipboard copy failed', copyError);
        }
    };

    const shareTicket = async (ticket) => {
        if (!ticket) {
            return;
        }
        const seats = Array.isArray(ticket.seatNumbers) ? ticket.seatNumbers : [];
        const message = `Booking #${ticket.bookingId}\nMovie: ${ticket.movieName || 'TBA'}\nTheatre: ${ticket.theatreName || 'TBA'}\nSeats: ${seats.join(', ')}`;
        if (navigator.share) {
            try {
                await navigator.share({
                    title: 'My Movie Ticket',
                    text: message
                });
            } catch (shareError) {
                if (shareError.name !== 'AbortError') {
                    console.warn('Share failed', shareError);
                }
            }
        } else {
            copyToClipboard(message);
        }
    };

    const handlePrint = (ticket) => {
        if (!ticket) {
            return;
        }
        const seats = Array.isArray(ticket.seatNumbers) ? ticket.seatNumbers : [];
        const totalCost = typeof ticket.totalCost === 'number' ? ticket.totalCost : 0;
        const printable = `Ticket Confirmation\n\nBooking Id: ${ticket.bookingId}\nMovie: ${ticket.movieName || 'TBA'}\nShow: ${ticket.showName || 'TBA'}\nTheatre: ${ticket.theatreName || 'TBA'}\nSeats: ${seats.join(', ')}\nAmount Paid: ₹${totalCost.toFixed(2)}`;
        const newWindow = window.open('', '_blank');
        if (newWindow) {
            newWindow.document.write(`<pre>${printable}</pre>`);
            newWindow.print();
            newWindow.close();
        }
    };

    if (loading) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" variant="primary" role="status" style={{ width: '3rem', height: '3rem' }}>
                    <span className="visually-hidden">Loading...</span>
                </Spinner>
                <h4 className="mt-3">Preparing your tickets...</h4>
                <p className="text-muted">Hang tight while we load your booking details.</p>
            </Container>
        );
    }

    return (
        <Container className="py-5">
            <div className="text-center mb-5">
                <h1 className="display-5 mb-2">🎟️ My Tickets</h1>
                <p className="text-muted mb-0">Track your upcoming shows and revisit your past movie nights.</p>
            </div>

            {error && (
                <Alert variant="danger" className="mb-4">
                    <Alert.Heading>🚫 Unable to load tickets</Alert.Heading>
                    <p className="mb-2">{error}</p>
                    <div className="d-flex gap-2">
                        <Button variant="outline-danger" onClick={clearHighlight}>
                            Dismiss
                        </Button>
                        <Button variant="primary" onClick={() => navigate(-1)}>
                            Go Back
                        </Button>
                    </div>
                </Alert>
            )}

            {tickets.length > 0 && (
                <Card className="mb-4 shadow-sm" style={{ borderRadius: '15px', border: 'none' }}>
                    <Card.Body className="p-4">
                        <Row className="gy-3 align-items-center">
                            <Col md={6}>
                                <div className="d-flex align-items-center gap-2">
                                    <strong>Filter:</strong>
                                    <ButtonGroup>
                                        {FILTER_OPTIONS.map((option) => (
                                            <ToggleButton
                                                key={option.key}
                                                id={`filter-${option.key}`}
                                                type="radio"
                                                variant={activeFilter === option.key ? 'primary' : 'outline-primary'}
                                                name="ticket-filters"
                                                value={option.key}
                                                checked={activeFilter === option.key}
                                                onChange={(e) => setActiveFilter(e.currentTarget.value)}
                                            >
                                                {option.label}
                                            </ToggleButton>
                                        ))}
                                    </ButtonGroup>
                                </div>
                            </Col>
                            <Col md={6}>
                                <Form>
                                    <Form.Control
                                        type="search"
                                        placeholder="Search by movie, theatre or reference"
                                        value={searchTerm}
                                        onChange={(e) => setSearchTerm(e.target.value)}
                                    />
                                </Form>
                            </Col>
                        </Row>
                    </Card.Body>
                </Card>
            )}

            {filteredTickets.length === 0 ? (
                <Card className="shadow-sm" style={{ borderRadius: '15px', border: 'none' }}>
                    <Card.Body className="text-center py-5">
                        <div style={{ fontSize: '4rem' }}>🍿</div>
                        <h4 className="mt-3">No tickets to show</h4>
                        <p className="text-muted mb-4">
                            {tickets.length === 0
                                ? 'You have not booked any tickets yet. Start exploring shows now!'
                                : 'No tickets match your filter. Try adjusting the filters above.'}
                        </p>
                        <Button variant="primary" onClick={() => navigate('/shows')}>
                            Find Shows
                        </Button>
                    </Card.Body>
                </Card>
            ) : (
                <Row className="g-4">
                    {filteredTickets.map((ticket) => {
                        const isHighlighted = highlightId === ticket.bookingId;
                        const totalCost = typeof ticket.totalCost === 'number' ? ticket.totalCost : 0;
                        const seatList = Array.isArray(ticket.seatNumbers) ? ticket.seatNumbers : [];
                        return (
                            <Col key={ticket.bookingId} md={6} xl={4}>
                                <Card
                                    className="h-100 shadow-sm"
                                    style={{
                                        borderRadius: '20px',
                                        border: isHighlighted ? '2px solid var(--primary-color)' : 'none',
                                        boxShadow: isHighlighted ? '0 0 0 4px rgba(102, 126, 234, 0.2)' : 'var(--shadow-soft)'
                                    }}
                                    onAnimationEnd={clearHighlight}
                                >
                                    <Card.Body className="d-flex flex-column">
                                        <div className="d-flex justify-content-between align-items-start mb-3">
                                            <div>
                                                <Badge bg="primary" className="mb-2">{ticket.transactionStatus || 'CONFIRMED'}</Badge>
                                                <h4 className="mb-0">{ticket.movieName || 'Movie Title'}</h4>
                                                <small className="text-muted">{ticket.showName || 'Showtime'}</small>
                                            </div>
                                            <div className="text-end">
                                                <strong>₹{totalCost.toFixed(2)}</strong>
                                                <br />
                                                <small className="text-muted">{ticket.transactionMode || 'ONLINE'}</small>
                                            </div>
                                        </div>

                                        <div className="mb-3">
                                            <div className="d-flex align-items-center gap-2 mb-1">
                                                <span role="img" aria-label="theatre">📍</span>
                                                <span>{ticket.theatreName || 'Theatre'}, {ticket.theatreCity || 'City'}</span>
                                            </div>
                                            <div className="d-flex align-items-center gap-2 mb-1">
                                                <span role="img" aria-label="screen">🖥️</span>
                                                <span>{ticket.screenName || 'Screen'} • {formatDateTime(ticket.showStartTime)}</span>
                                            </div>
                                            <div className="d-flex align-items-center gap-2">
                                                <span role="img" aria-label="seats">💺</span>
                                                <span>
                                                    {seatList.length > 0 ? seatList.join(', ') : `${ticket.seatCount || seatList.length} seats`}
                                                </span>
                                            </div>
                                        </div>

                                        <Card className="bg-light border-0 mb-3">
                                            <Card.Body className="py-2 px-3">
                                                <div className="d-flex justify-content-between align-items-center">
                                                    <div>
                                                        <small className="text-muted d-block">Payment Ref</small>
                                                        <strong>{ticket.paymentReference || 'N/A'}</strong>
                                                    </div>
                                                    <Button
                                                        variant="outline-secondary"
                                                        size="sm"
                                                        onClick={() => copyToClipboard(ticket.paymentReference || '')}
                                                    >
                                                        Copy
                                                    </Button>
                                                </div>
                                                <div className="d-flex justify-content-between align-items-center mt-2">
                                                    <div>
                                                        <small className="text-muted d-block">Booking Ref</small>
                                                        <strong>{ticket.bookingReference || 'N/A'}</strong>
                                                    </div>
                                                    <Button
                                                        variant="outline-secondary"
                                                        size="sm"
                                                        onClick={() => copyToClipboard(ticket.bookingReference ? ticket.bookingReference.toString() : '')}
                                                    >
                                                        Copy
                                                    </Button>
                                                </div>
                                            </Card.Body>
                                        </Card>

                                        <div className="mt-auto d-flex gap-2">
                                            <Button variant="primary" className="flex-grow-1" onClick={() => handlePrint(ticket)}>
                                                Download
                                            </Button>
                                            <Button variant="outline-primary" onClick={() => shareTicket(ticket)}>
                                                Share
                                            </Button>
                                        </div>
                                    </Card.Body>
                                </Card>
                            </Col>
                        );
                    })}
                </Row>
            )}
        </Container>
    );
}

export default TicketView;
