import React, { useState, useEffect, useCallback, useMemo } from 'react';
import { Row, Col, Card, Button, Alert, Table, Container, Badge } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

function CustomerDashboard() {
  const [sessionUser, setSessionUser] = useState(null);
  const [customer, setCustomer] = useState(null);
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const loadCustomerProfile = useCallback(async (customerId, fallbackProfile) => {
    if (!customerId) {
      return;
    }
    try {
      const response = await axios.get(`/api/customers/${customerId}`, { timeout: 10000 });
      setCustomer(response.data || fallbackProfile);
    } catch (err) {
      console.warn('Unable to load customer profile', err);
      setCustomer(fallbackProfile);
    }
  }, []);

  const fetchCustomerData = useCallback(async (customerId) => {
    const targetId = customerId ?? sessionUser?.userId;
    if (!targetId) {
      return;
    }

    try {
      setLoading(true);
      setError('');
      const response = await axios.get(`/api/bookings/customer/${targetId}`, { timeout: 10000 });
      setBookings(Array.isArray(response.data) ? response.data : []);
    } catch (err) {
      console.error('Error fetching bookings:', err);
      if (err.code === 'ECONNABORTED') {
        setError('Request timeout. Please check if the backend server is running.');
      } else if (err.response && err.response.status === 404) {
        setError('No bookings found for this account yet.');
        setBookings([]);
      } else if (err.request) {
        setError('Unable to connect to server. Please ensure the backend is running.');
      } else {
        setError('Failed to fetch booking data. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  }, [sessionUser]);

  useEffect(() => {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    if (!user.userId || user.role !== 'CUSTOMER') {
      navigate('/login');
      return;
    }

    setSessionUser(user);

    const fallbackProfile = {
      customerId: user.userId,
      customerName: user.name || user.firstName || user.username || user.email || 'Movie Lover',
      email: user.email
    };
    setCustomer(fallbackProfile);

    loadCustomerProfile(user.userId, fallbackProfile);
    fetchCustomerData(user.userId);
  }, [navigate, loadCustomerProfile, fetchCustomerData]);

  const getStatusBadge = (status) => {
    const variants = {
      'CONFIRMED': 'success',
      'PENDING': 'warning',
      'CANCELLED': 'danger',
      'COMPLETED': 'info'
    };
    return variants[status] || 'secondary';
  };

  const formatShowDate = (value) => {
    if (!value) {
      return 'Date TBD';
    }
    return new Date(value).toLocaleDateString('en-US', {
      weekday: 'short',
      month: 'short',
      day: 'numeric'
    });
  };

  const formatShowTime = (value) => {
    if (!value) {
      return '--';
    }
    return new Date(value).toLocaleTimeString('en-US', {
      hour: 'numeric',
      minute: '2-digit'
    });
  };

  const totalSpent = useMemo(() => bookings.reduce((sum, booking) => sum + (booking.totalCost || 0), 0), [bookings]);
  const uniqueMovies = useMemo(() => new Set(bookings.map((b) => b.movieId).filter(Boolean)).size, [bookings]);
  const upcomingShows = useMemo(() => {
    const now = new Date();
    return bookings.filter((booking) => booking.showStartTime && new Date(booking.showStartTime) >= now).length;
  }, [bookings]);

  const displayName = customer?.customerName
    || sessionUser?.name
    || sessionUser?.firstName
    || sessionUser?.username
    || 'Movie Lover';

  const handleRetry = () => {
    fetchCustomerData();
  };

  if (loading) {
    return (
      <Container className="text-center py-5">
        <div className="spinner-border text-primary" style={{ width: '3rem', height: '3rem' }}>
          <span className="visually-hidden">Loading...</span>
        </div>
        <h4 className="mt-3">Loading Dashboard...</h4>
        <p className="text-muted">Fetching your booking history</p>
      </Container>
    );
  }

  return (
    <Container className="py-5">
      {/* Welcome Header */}
      <div className="text-center mb-5">
        <h1 className="display-4 mb-3">👋 Welcome Back!</h1>
        <p className="lead">
          Hello {displayName}, manage your bookings and discover new movies
        </p>
      </div>

      {error && (
        <Alert variant="danger" className="mb-4">
          <Alert.Heading>🚫 Error</Alert.Heading>
          <p>{error}</p>
          <hr />
          <Button variant="outline-danger" onClick={handleRetry}>
            🔄 Retry
          </Button>
        </Alert>
      )}

      {/* Dashboard Stats */}
      <Row className="mb-5">
        <Col md={3} className="mb-4">
          <Card className="feature-card h-100 text-center">
            <Card.Body>
              <div className="feature-icon" style={{ fontSize: '3rem', marginBottom: '1rem' }}>🎫</div>
              <h3 className="text-primary mb-2">{bookings.length}</h3>
              <p className="mb-0">Total Bookings</p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3} className="mb-4">
          <Card className="feature-card h-100 text-center">
            <Card.Body>
              <div className="feature-icon" style={{ fontSize: '3rem', marginBottom: '1rem' }}>💰</div>
              <h3 className="text-success mb-2">
                ₹{totalSpent.toLocaleString()}
              </h3>
              <p className="mb-0">Total Spent</p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3} className="mb-4">
          <Card className="feature-card h-100 text-center">
            <Card.Body>
              <div className="feature-icon" style={{ fontSize: '3rem', marginBottom: '1rem' }}>🎬</div>
              <h3 className="text-info mb-2">
                {uniqueMovies}
              </h3>
              <p className="mb-0">Movies Watched</p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3} className="mb-4">
          <Card className="feature-card h-100 text-center">
            <Card.Body>
              <div className="feature-icon" style={{ fontSize: '3rem', marginBottom: '1rem' }}>⏰</div>
              <h3 className="text-warning mb-2">{upcomingShows}</h3>
              <p className="mb-0">Upcoming Shows</p>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Quick Actions */}
      <Card className="mb-5 shadow-sm" style={{ borderRadius: '15px', border: 'none' }}>
        <Card.Header className="bg-gradient text-white text-center py-3"
          style={{
            background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
            borderRadius: '15px 15px 0 0'
          }}>
          <h5 className="mb-0">🚀 Quick Actions</h5>
        </Card.Header>
        <Card.Body className="p-4">
          <Row>
            <Col md={3} className="mb-3">
              <Button
                variant="primary"
                className="w-100"
                onClick={() => navigate('/movies')}
                style={{ borderRadius: '15px', padding: '1rem' }}
              >
                🎭 Browse Movies
              </Button>
            </Col>
            <Col md={3} className="mb-3">
              <Button
                variant="outline-primary"
                className="w-100"
                onClick={() => navigate('/theatres')}
                style={{ borderRadius: '15px', padding: '1rem' }}
              >
                🏢 Find Theatres
              </Button>
            </Col>
            <Col md={3} className="mb-3">
              <Button
                variant="success"
                className="w-100"
                onClick={() => navigate('/shows')}
                style={{ borderRadius: '15px', padding: '1rem' }}
              >
                🎫 Book Tickets
              </Button>
            </Col>
            <Col md={3} className="mb-3">
              <Button
                variant="outline-success"
                className="w-100"
                onClick={() => navigate('/tickets')}
                style={{ borderRadius: '15px', padding: '1rem' }}
              >
                🎟️ View Tickets
              </Button>
            </Col>
            <Col md={3} className="mb-3">
              <Button
                variant="outline-success"
                className="w-100"
                onClick={handleRetry}
                style={{ borderRadius: '15px', padding: '1rem' }}
              >
                🔄 Refresh Data
              </Button>
            </Col>
          </Row>
        </Card.Body>
      </Card>

      {/* Booking History */}
      <Card className="shadow-sm" style={{ borderRadius: '15px', border: 'none' }}>
        <Card.Header className="bg-light text-center py-3" style={{ borderRadius: '15px 15px 0 0' }}>
          <h5 className="mb-0">📋 Your Booking History</h5>
        </Card.Header>
        <Card.Body className="p-0">
          {bookings.length === 0 ? (
            <div className="text-center p-5">
              <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>🎭</div>
              <h5>No Bookings Yet</h5>
              <p className="text-muted mb-4">
                Start your movie journey by booking your first show!
              </p>
              <Button
                variant="primary"
                onClick={() => navigate('/movies')}
                style={{ borderRadius: '25px' }}
              >
                🎬 Browse Movies
              </Button>
            </div>
          ) : (
            <div className="table-responsive">
              <Table hover className="mb-0">
                <thead className="bg-light">
                  <tr>
                    <th className="border-0 px-4 py-3">Booking ID</th>
                    <th className="border-0 px-4 py-3">Movie</th>
                    <th className="border-0 px-4 py-3">Theatre</th>
                    <th className="border-0 px-4 py-3">Date & Time</th>
                    <th className="border-0 px-4 py-3">Seats</th>
                    <th className="border-0 px-4 py-3">Amount</th>
                    <th className="border-0 px-4 py-3">Status</th>
                    <th className="border-0 px-4 py-3">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {bookings.map((booking, index) => (
                    <tr key={booking.bookingId || index}>
                      <td className="px-4 py-3">
                        <strong>#{booking.bookingId || `BK${index + 1001}`}</strong>
                      </td>
                      <td className="px-4 py-3">
                        <div>
                          <strong>{booking.movieName || 'Movie Title'}</strong>
                          <br />
                          <small className="text-muted">
                            {booking.movieGenre || 'Genre'} • {booking.language || 'Language'}
                          </small>
                        </div>
                      </td>
                      <td className="px-4 py-3">
                        <div>
                          <strong>{booking.theatreName || 'Theatre Name'}</strong>
                          <br />
                          <small className="text-muted">{booking.theatreCity || 'City'}</small>
                        </div>
                      </td>
                      <td className="px-4 py-3">
                        <div>
                          <strong>{formatShowDate(booking.showStartTime)}</strong>
                          <br />
                          <small className="text-muted">{formatShowTime(booking.showStartTime)}</small>
                        </div>
                      </td>
                      <td className="px-4 py-3">
                        <Badge bg="info">
                          {Array.isArray(booking.seatNumbers) && booking.seatNumbers.length > 0
                            ? booking.seatNumbers.join(', ')
                            : `${booking.seatCount ?? booking.noOfSeats ?? 0} seats`}
                        </Badge>
                      </td>
                      <td className="px-4 py-3">
                        <strong className="text-success">
                          ₹{Number(booking.totalCost ?? 0).toLocaleString()}
                        </strong>
                      </td>
                      <td className="px-4 py-3">
                        <Badge bg={getStatusBadge((booking.transactionStatus || '').toUpperCase())}>
                          {booking.transactionStatus || 'CONFIRMED'}
                        </Badge>
                      </td>
                      <td className="px-4 py-3">
                        <div className="d-flex gap-1">
                          <Button
                            variant="outline-primary"
                            size="sm"
                            style={{ borderRadius: '10px' }}
                            onClick={() => navigate('/tickets', { state: { bookingId: booking.bookingId } })}
                          >
                            📱 Ticket
                          </Button>
                          <Button
                            variant="outline-secondary"
                            size="sm"
                            style={{ borderRadius: '10px' }}
                          >
                            ⭐ Rate
                          </Button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </div>
          )}
        </Card.Body>
      </Card>

      {/* Recommendations Section */}
      <div className="cta-section mt-5">
        <h3 className="mb-3">🌟 Recommended for You</h3>
        <p className="mb-4">
          Based on your viewing history, we think you'll love these upcoming movies!
        </p>
        <Row>
          <Col md={6} className="mb-2">
            <Button
              variant="light"
              size="lg"
              onClick={() => navigate('/movies')}
              style={{ borderRadius: '25px' }}
            >
              🎬 Discover Movies
            </Button>
          </Col>
          <Col md={6} className="mb-2">
            <Button
              variant="outline-light"
              size="lg"
              onClick={() => navigate('/shows')}
              style={{ borderRadius: '25px' }}
            >
              🎭 Book Shows
            </Button>
          </Col>
        </Row>
      </div>
    </Container>
  );
}

export default CustomerDashboard;
