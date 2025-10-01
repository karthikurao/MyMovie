import React, { useState, useEffect } from 'react';
import { Row, Col, Card, Button, Alert, Form, Modal, Container, Badge } from 'react-bootstrap';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';

function BookTicket() {
  const { showId } = useParams();
  const [show, setShow] = useState(null);
  const [movie, setMovie] = useState(null);
  const [theatre, setTheatre] = useState(null);
  const [selectedSeats, setSelectedSeats] = useState([]);
  const [totalCost, setTotalCost] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [bookingSuccess, setBookingSuccess] = useState(false);
  const navigate = useNavigate();

  const seatPrices = {
    'A': 400, // Premium seats
    'B': 300, // Regular seats
    'C': 250, // Economy seats
  };

  const seatRows = ['A', 'B', 'C', 'D'];
  const seatsPerRow = 8;
  const occupiedSeats = ['A2', 'A5', 'B3', 'C1', 'C7']; // Mock occupied seats

  useEffect(() => {
    fetchShowDetails();
  }, [showId]);

  useEffect(() => {
    const cost = selectedSeats.reduce((total, seat) => {
      const row = seat.charAt(0);
      return total + (seatPrices[row] || seatPrices['C']);
    }, 0);
    setTotalCost(cost);
  }, [selectedSeats]);

  const fetchShowDetails = async () => {
    try {
      setLoading(true);
      setError('');

      // Fetch show details
      const showResponse = await axios.get(`/api/shows/${showId}`, { timeout: 10000 });
      setShow(showResponse.data);

      // Fetch related movie and theatre details
      if (showResponse.data.movieId) {
        const movieResponse = await axios.get(`/api/movies/${showResponse.data.movieId}`);
        setMovie(movieResponse.data);
      }

      if (showResponse.data.theatreId) {
        const theatreResponse = await axios.get(`/api/theatres/${showResponse.data.theatreId}`);
        setTheatre(theatreResponse.data);
      }
    } catch (err) {
      console.error('Error fetching show details:', err);
      if (err.code === 'ECONNABORTED') {
        setError('Request timeout. Please check if the backend server is running.');
      } else if (err.request) {
        setError('Unable to connect to server. Please ensure the backend is running.');
      } else {
        setError('Failed to fetch show details. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleSeatToggle = (seat) => {
    if (occupiedSeats.includes(seat)) return; // Can't select occupied seats

    setSelectedSeats(prev =>
      prev.includes(seat)
        ? prev.filter(s => s !== seat)
        : [...prev, seat]
    );
  };

  const getSeatStatus = (seat) => {
    if (occupiedSeats.includes(seat)) return 'occupied';
    if (selectedSeats.includes(seat)) return 'selected';
    return 'available';
  };

  const getSeatPrice = (seat) => {
    const row = seat.charAt(0);
    return seatPrices[row] || seatPrices['C'];
  };

  const handleBooking = async () => {
    if (selectedSeats.length === 0) {
      setError('Please select at least one seat.');
      return;
    }

    const user = JSON.parse(localStorage.getItem('user') || '{}');
    if (!user.userId) {
      navigate('/login');
      return;
    }

    try {
      const bookingData = {
        showId: parseInt(showId),
        customerId: user.userId,
        seatNumbers: selectedSeats,
        totalCost: totalCost,
        bookingDate: new Date().toISOString().split('T')[0]
      };

      await axios.post('/api/bookings', bookingData);
      setBookingSuccess(true);
      setShowModal(true);
    } catch (err) {
      setError('Booking failed. Please try again.');
    }
  };

  const formatDateTime = (dateTime) => {
    return new Date(dateTime).toLocaleString('en-US', {
      weekday: 'long',
      month: 'long',
      day: 'numeric',
      hour: 'numeric',
      minute: '2-digit',
      hour12: true
    });
  };

  if (loading) {
    return (
      <Container className="text-center py-5">
        <div className="spinner-border text-primary" style={{ width: '3rem', height: '3rem' }}>
          <span className="visually-hidden">Loading...</span>
        </div>
        <h4 className="mt-3">Loading Show Details...</h4>
        <p className="text-muted">Preparing your booking experience</p>
      </Container>
    );
  }

  if (error) {
    return (
      <Container className="py-5">
        <Alert variant="danger" className="text-center">
          <Alert.Heading>🚫 Error</Alert.Heading>
          <p>{error}</p>
          <hr />
          <div className="d-flex justify-content-center gap-2">
            <Button variant="outline-danger" onClick={fetchShowDetails}>
              🔄 Retry
            </Button>
            <Button variant="outline-primary" onClick={() => navigate('/shows')}>
              ← Back to Shows
            </Button>
          </div>
        </Alert>
      </Container>
    );
  }

  return (
    <Container className="py-5">
      <div className="text-center mb-5">
        <h1 className="display-4 mb-3">🎟️ Book Your Tickets</h1>
        <p className="lead">Select your preferred seats and complete your booking</p>
      </div>

      {/* Show Information Card */}
      <Card className="mb-5 shadow-sm" style={{ borderRadius: '15px', border: 'none' }}>
        <Card.Header className="bg-gradient text-white py-4"
                    style={{
                      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                      borderRadius: '15px 15px 0 0'
                    }}>
          <Row className="align-items-center">
            <Col md={8}>
              <h4 className="mb-1">{movie ? movie.movieName : 'Movie Details'}</h4>
              <p className="mb-0">
                {show && formatDateTime(show.showStartTime)}
              </p>
            </Col>
            <Col md={4} className="text-md-end">
              <Badge bg="light" text="dark" className="px-3 py-2">
                🏢 {theatre ? theatre.theatreName : 'Theatre'}
              </Badge>
            </Col>
          </Row>
        </Card.Header>
        <Card.Body className="p-4">
          <Row>
            <Col md={6}>
              <h6><strong>🎬 Movie Details:</strong></h6>
              <p className="mb-1">
                <Badge className="movie-genre me-2">
                  {movie ? movie.movieGenre : 'Genre'}
                </Badge>
                <Badge bg="info">{movie ? movie.language : 'Language'}</Badge>
              </p>
              <p className="mb-3"><strong>Duration:</strong> {movie ? movie.movieHours : 'N/A'}</p>
            </Col>
            <Col md={6}>
              <h6><strong>🏢 Theatre Details:</strong></h6>
              <p className="mb-1"><strong>Location:</strong> {theatre ? theatre.theatreCity : 'City'}</p>
              <p className="mb-1"><strong>Show:</strong> {show ? show.showName : 'Show Name'}</p>
              <p className="mb-0"><strong>Screen:</strong> Screen {show ? show.screenId : '1'}</p>
            </Col>
          </Row>
        </Card.Body>
      </Card>

      {/* Seat Selection */}
      <Card className="mb-4 shadow-sm" style={{ borderRadius: '15px', border: 'none' }}>
        <Card.Header className="bg-light text-center py-3" style={{ borderRadius: '15px 15px 0 0' }}>
          <h5 className="mb-0">🎭 Select Your Seats</h5>
          <small className="text-muted">Click on available seats to select/deselect</small>
        </Card.Header>
        <Card.Body className="p-4">
          {/* Screen Indicator */}
          <div className="text-center mb-4">
            <div className="screen-indicator">
              <div style={{
                background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                color: 'white',
                padding: '10px 40px',
                borderRadius: '50px',
                display: 'inline-block',
                marginBottom: '10px'
              }}>
                🎬 SCREEN
              </div>
            </div>
          </div>

          {/* Seat Map */}
          <div className="seat-map">
            {seatRows.map(row => (
              <div key={row} className="seat-row mb-3">
                <Row className="justify-content-center align-items-center">
                  <Col xs="auto">
                    <strong className="seat-row-label">{row}</strong>
                  </Col>
                  <Col xs="auto">
                    <div className="d-flex gap-2">
                      {Array.from({ length: seatsPerRow }, (_, i) => {
                        const seatNumber = `${row}${i + 1}`;
                        const status = getSeatStatus(seatNumber);
                        const price = getSeatPrice(seatNumber);

                        return (
                          <button
                            key={seatNumber}
                            className={`seat-btn seat-${status}`}
                            onClick={() => handleSeatToggle(seatNumber)}
                            disabled={status === 'occupied'}
                            title={`Seat ${seatNumber} - ₹${price}`}
                            style={{
                              width: '40px',
                              height: '40px',
                              border: 'none',
                              borderRadius: '8px',
                              fontSize: '12px',
                              fontWeight: 'bold',
                              cursor: status === 'occupied' ? 'not-allowed' : 'pointer',
                              backgroundColor:
                                status === 'selected' ? '#667eea' :
                                status === 'occupied' ? '#dc3545' : '#e9ecef',
                              color:
                                status === 'selected' ? 'white' :
                                status === 'occupied' ? 'white' : '#495057',
                              transition: 'all 0.3s ease',
                              transform: status === 'selected' ? 'scale(1.1)' : 'scale(1)'
                            }}
                          >
                            {i + 1}
                          </button>
                        );
                      })}
                    </div>
                  </Col>
                  <Col xs="auto">
                    <Badge bg="outline-secondary">₹{seatPrices[row]}</Badge>
                  </Col>
                </Row>
              </div>
            ))}
          </div>

          {/* Seat Legend */}
          <div className="seat-legend mt-4 text-center">
            <Row className="justify-content-center">
              <Col xs="auto">
                <div className="d-flex align-items-center me-3">
                  <div style={{ width: '20px', height: '20px', backgroundColor: '#e9ecef', borderRadius: '4px', marginRight: '8px' }}></div>
                  <small>Available</small>
                </div>
              </Col>
              <Col xs="auto">
                <div className="d-flex align-items-center me-3">
                  <div style={{ width: '20px', height: '20px', backgroundColor: '#667eea', borderRadius: '4px', marginRight: '8px' }}></div>
                  <small>Selected</small>
                </div>
              </Col>
              <Col xs="auto">
                <div className="d-flex align-items-center">
                  <div style={{ width: '20px', height: '20px', backgroundColor: '#dc3545', borderRadius: '4px', marginRight: '8px' }}></div>
                  <small>Occupied</small>
                </div>
              </Col>
            </Row>
          </div>
        </Card.Body>
      </Card>

      {/* Booking Summary */}
      {selectedSeats.length > 0 && (
        <Card className="mb-4 shadow-sm" style={{ borderRadius: '15px', border: 'none' }}>
          <Card.Header className="bg-success text-white py-3" style={{ borderRadius: '15px 15px 0 0' }}>
            <h5 className="mb-0">🧾 Booking Summary</h5>
          </Card.Header>
          <Card.Body className="p-4">
            <Row>
              <Col md={8}>
                <h6>Selected Seats:</h6>
                <p className="mb-2">
                  {selectedSeats.map(seat => (
                    <Badge key={seat} bg="primary" className="me-2 mb-1">
                      {seat} (₹{getSeatPrice(seat)})
                    </Badge>
                  ))}
                </p>
                <p className="mb-0">
                  <strong>Total Seats:</strong> {selectedSeats.length}
                </p>
              </Col>
              <Col md={4} className="text-md-end">
                <h4 className="text-success mb-3">
                  Total: ₹{totalCost.toLocaleString()}
                </h4>
                <Button
                  variant="success"
                  size="lg"
                  onClick={handleBooking}
                  className="w-100"
                  style={{ borderRadius: '25px' }}
                >
                  💳 Confirm Booking
                </Button>
              </Col>
            </Row>
          </Card.Body>
        </Card>
      )}

      {/* Success Modal */}
      <Modal show={showModal} onHide={() => setShowModal(false)} centered>
        <Modal.Header closeButton style={{ background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', color: 'white' }}>
          <Modal.Title>🎉 Booking Confirmed!</Modal.Title>
        </Modal.Header>
        <Modal.Body className="text-center p-4">
          <div className="mb-3">
            <div style={{ fontSize: '4rem' }}>🎟️</div>
          </div>
          <h5>Your tickets have been booked successfully!</h5>
          <p className="text-muted">
            You will receive a confirmation email shortly.
          </p>
          <div className="mt-3">
            <Badge bg="success" className="me-2">Seats: {selectedSeats.join(', ')}</Badge>
            <Badge bg="info">Total: ₹{totalCost.toLocaleString()}</Badge>
          </div>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="outline-primary" onClick={() => navigate('/shows')}>
            Book More Shows
          </Button>
          <Button variant="primary" onClick={() => navigate('/customer-dashboard')}>
            View My Bookings
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
}

export default BookTicket;
