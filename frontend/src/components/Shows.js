import React, { useState, useEffect } from 'react';
import { Row, Col, Card, Button, Alert, Spinner, Form, Container, Badge } from 'react-bootstrap';
import { useSearchParams, useNavigate } from 'react-router-dom';
import axios from 'axios';

function Shows() {
  const [shows, setShows] = useState([]);
  const [theatres, setTheatres] = useState([]);
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedTheatre, setSelectedTheatre] = useState('');
  const [selectedMovie, setSelectedMovie] = useState('');
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    fetchData();
    const movieId = searchParams.get('movieId');
    const theatreId = searchParams.get('theatreId');
    if (movieId) setSelectedMovie(movieId);
    if (theatreId) setSelectedTheatre(theatreId);
  }, [searchParams]);

  const fetchData = async () => {
    try {
      setLoading(true);
      setError('');
      const [showsRes, theatresRes, moviesRes] = await Promise.all([
        axios.get('/api/shows', { timeout: 10000 }),
        axios.get('/api/theatres', { timeout: 10000 }),
        axios.get('/api/movies', { timeout: 10000 })
      ]);

      setShows(showsRes.data);
      setTheatres(theatresRes.data);
      setMovies(moviesRes.data);
    } catch (err) {
      console.error('Error fetching shows data:', err);
      if (err.code === 'ECONNABORTED') {
        setError('Request timeout. Please check if the backend server is running.');
      } else if (err.request) {
        setError('Unable to connect to server. Please ensure the backend is running.');
      } else {
        setError('Failed to fetch shows. Please try again later.');
      }
    } finally {
      setLoading(false);
    }
  };

  const filteredShows = shows.filter(show => {
    return (!selectedTheatre || show.theatreId == selectedTheatre) &&
           (!selectedMovie || show.movieId == selectedMovie);
  });

  const handleBookTicket = (showId) => {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    if (!user.userId) {
      navigate('/login');
      return;
    }
    navigate(`/book-ticket/${showId}`);
  };

  const formatDateTime = (dateTime) => {
    return new Date(dateTime).toLocaleString('en-US', {
      weekday: 'short',
      month: 'short',
      day: 'numeric',
      hour: 'numeric',
      minute: '2-digit',
      hour12: true
    });
  };

  if (loading) {
    return (
      <Container className="text-center py-5">
        <Spinner animation="border" role="status" style={{ width: '3rem', height: '3rem' }}>
          <span className="visually-hidden">Loading...</span>
        </Spinner>
        <h4 className="mt-3">Loading Shows...</h4>
        <p className="text-muted">Finding the best shows for you</p>
      </Container>
    );
  }

  if (error) {
    return (
      <Container className="py-5">
        <Alert variant="danger" className="text-center">
          <Alert.Heading>🚫 Connection Error</Alert.Heading>
          <p>{error}</p>
          <hr />
          <div className="d-flex justify-content-center">
            <Button variant="outline-danger" onClick={fetchData}>
              🔄 Retry
            </Button>
          </div>
        </Alert>
      </Container>
    );
  }

  return (
    <Container className="py-5">
      <div className="text-center mb-5">
        <h1 className="display-4 mb-3">🎭 Available Shows</h1>
        <p className="lead">Book your tickets for the best movie experiences</p>
      </div>

      {/* Enhanced Filters Section */}
      <Card className="mb-5 shadow-sm" style={{ borderRadius: '15px', border: 'none' }}>
        <Card.Header className="bg-gradient text-white text-center py-3"
                    style={{
                      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                      borderRadius: '15px 15px 0 0'
                    }}>
          <h5 className="mb-0">🔍 Filter Shows</h5>
        </Card.Header>
        <Card.Body className="p-4">
          <Row>
            <Col md={6} className="mb-3">
              <Form.Group>
                <Form.Label className="fw-bold">🏢 Filter by Theatre</Form.Label>
                <Form.Select
                  value={selectedTheatre}
                  onChange={(e) => setSelectedTheatre(e.target.value)}
                  className="form-control"
                >
                  <option value="">All Theatres</option>
                  {theatres.map(theatre => (
                    <option key={theatre.theatreId} value={theatre.theatreId}>
                      {theatre.theatreName} - {theatre.theatreCity}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md={6} className="mb-3">
              <Form.Group>
                <Form.Label className="fw-bold">🎬 Filter by Movie</Form.Label>
                <Form.Select
                  value={selectedMovie}
                  onChange={(e) => setSelectedMovie(e.target.value)}
                  className="form-control"
                >
                  <option value="">All Movies</option>
                  {movies.map(movie => (
                    <option key={movie.movieId} value={movie.movieId}>
                      {movie.movieName} ({movie.language})
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
          </Row>
        </Card.Body>
      </Card>

      {/* Shows Display */}
      {filteredShows.length === 0 ? (
        <Alert variant="info" className="text-center">
          <Alert.Heading>🎭 No Shows Available</Alert.Heading>
          <p>No shows match your current filters. Try adjusting your selection.</p>
          <hr />
          <Button variant="outline-info" onClick={() => {
            setSelectedTheatre('');
            setSelectedMovie('');
          }}>
            🔄 Clear Filters
          </Button>
        </Alert>
      ) : (
        <>
          <div className="mb-4">
            <Badge bg="primary" className="me-2">
              {filteredShows.length} Shows Available
            </Badge>
            <Badge bg="success">
              🎯 Ready to Book
            </Badge>
          </div>

          <Row>
            {filteredShows.map((show) => {
              const movie = movies.find(m => m.movieId === show.movieId);
              const theatre = theatres.find(t => t.theatreId === show.theatreId);

              return (
                <Col key={show.showId} lg={6} className="mb-4">
                  <Card className="movie-card h-100 show-card">
                    <Card.Body className="p-4">
                      <div className="d-flex justify-content-between align-items-start mb-3">
                        <div>
                          <h5 className="movie-title mb-2">
                            {movie ? movie.movieName : 'Unknown Movie'}
                          </h5>
                          <Badge className="movie-genre me-2">
                            {movie ? movie.movieGenre : 'Unknown'}
                          </Badge>
                          <Badge bg="info">
                            {movie ? movie.language : 'Unknown'}
                          </Badge>
                        </div>
                        <div className="rating-stars">★★★★☆</div>
                      </div>

                      <div className="mb-3">
                        <p className="mb-2">
                          <strong>🏢 Theatre:</strong> {theatre ? theatre.theatreName : 'Unknown Theatre'}
                          {theatre && <small className="text-muted"> ({theatre.theatreCity})</small>}
                        </p>
                        <p className="mb-2">
                          <strong>🎪 Show:</strong> {show.showName}
                        </p>
                        <p className="mb-2">
                          <strong>⏰ Start Time:</strong> {formatDateTime(show.showStartTime)}
                        </p>
                        <p className="mb-2">
                          <strong>⏱️ End Time:</strong> {formatDateTime(show.showEndTime)}
                        </p>
                        {movie && (
                          <p className="mb-0">
                            <strong>🕐 Duration:</strong> {movie.movieHours}
                          </p>
                        )}
                      </div>

                      <div className="d-flex gap-2">
                        <Button
                          variant="primary"
                          onClick={() => handleBookTicket(show.showId)}
                          className="flex-fill"
                        >
                          🎫 Book Tickets
                        </Button>
                        <Button
                          variant="outline-primary"
                          onClick={() => navigate(`/movies`)}
                        >
                          📝 Details
                        </Button>
                      </div>
                    </Card.Body>
                  </Card>
                </Col>
              );
            })}
          </Row>
        </>
      )}
    </Container>
  );
}

export default Shows;
