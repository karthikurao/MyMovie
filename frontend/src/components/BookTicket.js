import React, { useState, useEffect, useCallback } from 'react';
import { Row, Col, Card, Button, Alert, Modal, Container, Badge, Spinner } from 'react-bootstrap';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { loadStripe } from '@stripe/stripe-js';
import { Elements, CardElement, useElements, useStripe } from '@stripe/react-stripe-js';

const SEAT_PRICES = {
  A: 400,
  B: 300,
  C: 250
};

const SEAT_ROWS = ['A', 'B', 'C', 'D'];
const SEATS_PER_ROW = 8;
const MOCK_OCCUPIED_SEATS = ['A2', 'A5', 'B3', 'C1', 'C7'];

const publishableKey = process.env.REACT_APP_STRIPE_PUBLISHABLE_KEY;
const stripePromise = publishableKey ? loadStripe(publishableKey) : null;

const CARD_ELEMENT_OPTIONS = {
  hidePostalCode: true,
  style: {
    base: {
      fontSize: '16px',
      color: '#32325d',
      fontFamily: '"Helvetica Neue", Helvetica, sans-serif',
      '::placeholder': {
        color: '#a0aec0'
      }
    },
    invalid: {
      color: '#fa755a'
    }
  }
};

function CheckoutForm({ clientSecret, amount, customer, onSuccess, onCancel, reportError }) {
  const stripe = useStripe();
  const elements = useElements();
  const [cardError, setCardError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!stripe || !elements) {
      return;
    }

    setIsSubmitting(true);
    setCardError('');

    const cardElement = elements.getElement(CardElement);
    if (!cardElement) {
      setCardError('Card details could not be loaded. Please refresh and try again.');
      setIsSubmitting(false);
      return;
    }

    const { error, paymentIntent } = await stripe.confirmCardPayment(clientSecret, {
      payment_method: {
        card: cardElement,
        billing_details: {
          name: customer?.customerName,
          email: customer?.email
        }
      }
    });

    if (error) {
      const message = error.message || 'Payment failed. Please try another card.';
      setCardError(message);
      reportError(message);
      setIsSubmitting(false);
      return;
    }

    if (paymentIntent && paymentIntent.status === 'succeeded') {
      try {
        await onSuccess(paymentIntent);
      } finally {
        setIsSubmitting(false);
      }
    } else {
      const message = 'Payment was not completed. Please try again.';
      setCardError(message);
      reportError(message);
      setIsSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="p-3 border rounded">
        <CardElement options={CARD_ELEMENT_OPTIONS} onChange={(event) => {
          if (event.error) {
            setCardError(event.error.message || 'Invalid card details');
          } else {
            setCardError('');
          }
        }} />
      </div>
      {cardError && (
        <Alert variant="danger" className="mt-3 mb-0">
          {cardError}
        </Alert>
      )}
      <div className="d-flex justify-content-between align-items-center mt-4">
        <Button variant="outline-secondary" onClick={onCancel} disabled={isSubmitting}>
          Cancel
        </Button>
        <Button type="submit" variant="primary" disabled={!stripe || isSubmitting}>
          {isSubmitting ? (
            <>
              <Spinner animation="border" size="sm" className="me-2" />
              Processing...
            </>
          ) : (
            `Pay ₹${amount.toLocaleString()}`
          )}
        </Button>
      </div>
    </form>
  );
}

function BookTicket() {
  const { showId } = useParams();
  const [show, setShow] = useState(null);
  const [movie, setMovie] = useState(null);
  const [theatre, setTheatre] = useState(null);
  const [customer, setCustomer] = useState(null);
  const [selectedSeats, setSelectedSeats] = useState([]);
  const [totalCost, setTotalCost] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const [paymentIntentSecret, setPaymentIntentSecret] = useState('');
  const [paymentIntentId, setPaymentIntentId] = useState('');
  const [isPaymentIntentLoading, setIsPaymentIntentLoading] = useState(false);
  const [paymentError, setPaymentError] = useState('');
  const [paymentReference, setPaymentReference] = useState('');
  const navigate = useNavigate();

  const resetPaymentState = () => {
    setShowPaymentModal(false);
    setPaymentIntentSecret('');
    setPaymentIntentId('');
    setIsPaymentIntentLoading(false);
    setPaymentError('');
  };

  const fetchShowDetails = useCallback(async () => {
    try {
      setLoading(true);
      setError('');

      const showResponse = await axios.get(`/api/shows/${showId}`, { timeout: 10000 });
      setShow(showResponse.data);

      if (showResponse.data.movieId) {
        const movieResponse = await axios.get(`/api/movies/${showResponse.data.movieId}`);
        setMovie(movieResponse.data);
      } else {
        setMovie(null);
      }

      if (showResponse.data.theatreId) {
        const theatreResponse = await axios.get(`/api/theatres/${showResponse.data.theatreId}`);
        setTheatre(theatreResponse.data);
      } else {
        setTheatre(null);
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
  }, [showId]);

  useEffect(() => {
    const verifyCustomer = async () => {
      const storedUser = JSON.parse(localStorage.getItem('user') || '{}');

      if (!storedUser.userId) {
        setError('Please sign in to book tickets.');
        navigate('/login');
        return;
      }

      if (storedUser.role !== 'CUSTOMER') {
        setError('Only customer accounts can book tickets.');
        navigate('/login');
        return;
      }

      try {
        const response = await axios.get(`/api/customers/${storedUser.userId}`, { timeout: 10000 });
        setCustomer(response.data);
      } catch (err) {
        console.error('Unable to verify customer profile:', err);
        setError('Your customer profile could not be found. Please sign in again.');
        localStorage.removeItem('user');
        navigate('/login');
      }
    };

    verifyCustomer();
  }, [navigate]);

  useEffect(() => {
    fetchShowDetails();
  }, [fetchShowDetails]);

  useEffect(() => {
    const cost = selectedSeats.reduce((total, seat) => {
      const row = seat.charAt(0);
      return total + (SEAT_PRICES[row] ?? SEAT_PRICES.C);
    }, 0);
    setTotalCost(cost);
  }, [selectedSeats]);
  const handleSeatToggle = (seat) => {
    if (MOCK_OCCUPIED_SEATS.includes(seat)) return; // Can't select occupied seats

    setSelectedSeats(prev =>
      prev.includes(seat)
        ? prev.filter(s => s !== seat)
        : [...prev, seat]
    );
  };

  const getSeatStatus = (seat) => {
    if (MOCK_OCCUPIED_SEATS.includes(seat)) return 'occupied';
    if (selectedSeats.includes(seat)) return 'selected';
    return 'available';
  };

  const getSeatPrice = (seat) => {
    const row = seat.charAt(0);
    return SEAT_PRICES[row] ?? SEAT_PRICES.C;
  };

  const handleInitiateCheckout = async () => {
    if (selectedSeats.length === 0) {
      setError('Please select at least one seat.');
      return;
    }

    if (!customer) {
      setError('Unable to verify your customer profile. Please sign in again.');
      navigate('/login');
      return;
    }

    if (!publishableKey || !stripePromise) {
      setPaymentError('Payment configuration is missing. Please add the Stripe publishable key.');
      return;
    }

    if (totalCost <= 0) {
      setPaymentError('Unable to calculate the total cost. Please re-select your seats.');
      return;
    }

    try {
      setIsPaymentIntentLoading(true);
      setError('');
      setPaymentError('');
      setPaymentReference('');

      const amountInPaise = Math.round(totalCost * 100);
      const seatSummary = selectedSeats.join(', ');
      const description = `Booking for show ${show?.showName ?? showId} - Seats: ${seatSummary}`;

      const response = await axios.post('/api/payments/create-intent', {
        amount: amountInPaise,
        currency: 'inr',
        receiptEmail: customer.email,
        description
      });

      setPaymentIntentSecret(response.data.clientSecret);
      setPaymentIntentId(response.data.paymentIntentId);
      setShowPaymentModal(true);
    } catch (err) {
      console.error('Failed to create payment intent:', err);
      const message = err.response?.data?.error
        || (err.message ? `Unable to initiate payment: ${err.message}` : 'Unable to initiate payment. Please try again.');
      setPaymentError(message);
    } finally {
      setIsPaymentIntentLoading(false);
    }
  };

  const submitBooking = async (paymentIntentIdentifier) => {
    const bookingData = {
      showId: parseInt(showId, 10),
      customerId: customer.customerId,
      seatNumbers: selectedSeats,
      totalCost,
      bookingDate: new Date().toISOString().split('T')[0],
      paymentMode: 'CARD',
      paymentIntentId: paymentIntentIdentifier
    };

    await axios.post('/api/bookings', bookingData);
  };

  const handlePaymentSuccess = async (paymentIntent) => {
    try {
      await submitBooking(paymentIntent.id);
      setPaymentReference(paymentIntent.id);
      setError('');
      setPaymentError('');
      setShowModal(true);
      setSelectedSeats([]);
      resetPaymentState();
    } catch (err) {
      console.error('Payment succeeded but booking failed:', err);
      const message = 'Payment succeeded but booking could not be completed. Please contact support with your payment reference.';
      setPaymentError(message);
      setError(message);
    }
  };

  const handleClosePaymentModal = () => {
    if (isPaymentIntentLoading) {
      return;
    }
    resetPaymentState();
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
            {SEAT_ROWS.map(row => (
              <div key={row} className="seat-row mb-3">
                <Row className="justify-content-center align-items-center">
                  <Col xs="auto">
                    <strong className="seat-row-label">{row}</strong>
                  </Col>
                  <Col xs="auto">
                    <div className="d-flex gap-2">
                      {Array.from({ length: SEATS_PER_ROW }, (_, i) => {
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
                    <Badge bg="outline-secondary">₹{SEAT_PRICES[row] ?? SEAT_PRICES.C}</Badge>
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
                {paymentError && !showPaymentModal && (
                  <Alert variant="warning" className="text-start">
                    {paymentError}
                  </Alert>
                )}
                <Button
                  variant="success"
                  size="lg"
                  onClick={handleInitiateCheckout}
                  className="w-100"
                  style={{ borderRadius: '25px' }}
                  disabled={isPaymentIntentLoading}
                >
                  {isPaymentIntentLoading ? (
                    <>
                      <Spinner animation="border" size="sm" className="me-2" />
                      Starting Payment...
                    </>
                  ) : (
                    '💳 Proceed to Payment'
                  )}
                </Button>
              </Col>
            </Row>
          </Card.Body>
        </Card>
      )}
      {/* Payment Modal */}
      <Modal show={showPaymentModal} onHide={handleClosePaymentModal} centered backdrop="static">
        <Modal.Header closeButton style={{ background: 'linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%)', color: 'white' }}>
          <Modal.Title>Complete Your Payment</Modal.Title>
        </Modal.Header>
        <Modal.Body className="p-4">
          <p className="mb-3 text-muted">Enter your card details to pay ₹{totalCost.toLocaleString()} for the selected seats.</p>
          {paymentIntentId && (
            <div className="mb-3">
              <small className="text-muted">Payment Reference (temporary):</small>
              <div className="fw-semibold" style={{ wordBreak: 'break-all' }}>{paymentIntentId}</div>
            </div>
          )}
          {paymentError && (
            <Alert variant="danger" className="mb-3">
              {paymentError}
            </Alert>
          )}
          {stripePromise && paymentIntentSecret ? (
            <Elements stripe={stripePromise} options={{ clientSecret: paymentIntentSecret }}>
              <CheckoutForm
                clientSecret={paymentIntentSecret}
                amount={totalCost}
                customer={customer}
                onSuccess={handlePaymentSuccess}
                onCancel={handleClosePaymentModal}
                reportError={setPaymentError}
              />
            </Elements>
          ) : (
            <div className="text-center py-4">
              <Spinner animation="border" role="status" />
              <p className="mt-3 mb-0">Preparing secure payment form...</p>
            </div>
          )}
        </Modal.Body>
      </Modal>

      {/* Success Modal */}
      <Modal show={showModal} onHide={() => { setShowModal(false); setPaymentReference(''); }} centered>
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
          {paymentReference && (
            <Alert variant="success">
              Payment reference: <strong>{paymentReference}</strong>
            </Alert>
          )}
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
