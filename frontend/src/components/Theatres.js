import React, { useState, useEffect } from 'react';
import { Row, Col, Card, Alert, Spinner, Container, Badge, Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

function Theatres() {
  const [theatres, setTheatres] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  // Theatre images for different cities/types
  const getTheatreImage = (theatreName, city) => {
    const theatreImages = [
      '/assets/theatres/aurora-lounge.svg',
      '/assets/theatres/velvet-premiere.svg',
      '/assets/theatres/celestial-screen.svg',
      '/assets/theatres/regal-grand.svg',
      '/assets/theatres/neon-dream.svg',
      '/assets/theatres/platinum-suite.svg'
    ];

    const hash = (theatreName + city).split('').reduce((a, b) => a + b.charCodeAt(0), 0);
    return theatreImages[hash % theatreImages.length];
  };

  useEffect(() => {
    fetchTheatres();
  }, []);

  const fetchTheatres = async () => {
    try {
      setLoading(true);
      setError('');
      const response = await axios.get('/api/theatres', {
        timeout: 10000,
        headers: {
          'Content-Type': 'application/json',
        }
      });
      setTheatres(response.data);
    } catch (err) {
      console.error('Error fetching theatres:', err);
      if (err.code === 'ECONNABORTED') {
        setError('Request timeout. Please check if the backend server is running on port 8080.');
      } else if (err.response) {
        setError(`Server error: ${err.response.status} - ${err.response.statusText}`);
      } else if (err.request) {
        setError('Unable to connect to server. Please ensure the backend is running on http://localhost:8080');
      } else {
        setError('An unexpected error occurred. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Container className="text-center py-5">
        <Spinner animation="border" role="status" style={{ width: '3rem', height: '3rem' }}>
          <span className="visually-hidden">Loading...</span>
        </Spinner>
        <h4 className="mt-3">Loading Theatres...</h4>
        <p className="text-muted">Finding the best theatres for you</p>
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
            <Button variant="outline-danger" onClick={fetchTheatres}>
              🔄 Retry
            </Button>
          </div>
        </Alert>
      </Container>
    );
  }

  return (
    <Container className="py-5">
      <div className="text-center mb-5 page-heading-dark">
        <h1 className="display-4 mb-3">🏢 Premium Theatres</h1>
        <p className="lead">Experience movies in world-class theatres with luxury amenities</p>
      </div>

      {theatres.length === 0 ? (
        <Alert variant="info" className="text-center">
          <Alert.Heading>🎭 No Theatres Available</Alert.Heading>
          <p>We're expanding our theatre network. Please check back soon!</p>
          <hr />
          <Button variant="outline-info" onClick={fetchTheatres}>
            🔄 Refresh
          </Button>
        </Alert>
      ) : (
        <>
          <div className="mb-4">
            <Badge bg="primary" className="me-2">
              {theatres.length} Theatres Available
            </Badge>
            <Badge bg="success">
              🌟 Premium Experience
            </Badge>
          </div>

          <Row>
            {theatres.map((theatre) => (
              <Col key={theatre.theatreId} lg={4} md={6} className="mb-4">
                <Card className="theatre-card h-100 surface-dark">
                  <Card.Img
                    variant="top"
                    src={getTheatreImage(theatre.theatreName, theatre.theatreCity)}
                    style={{ height: '250px', objectFit: 'cover' }}
                    alt={theatre.theatreName}
                    onError={(e) => {
                      e.target.src = '/assets/theatres/aurora-lounge.svg';
                    }}
                  />
                  <Card.Body className="d-flex flex-column">
                    <Card.Title className="movie-title">{theatre.theatreName}</Card.Title>
                    <div className="mb-3">
                      <Badge bg="info" className="me-2">📍 {theatre.theatreCity}</Badge>
                      <Badge bg="warning">⭐ Premium</Badge>
                    </div>
                    <Card.Text className="flex-grow-1">
                      <div className="mb-2">
                        <strong>🎬 Features:</strong>
                        <ul className="list-unstyled mt-1">
                          <li>• Dolby Atmos Sound System</li>
                          <li>• Luxury Recliner Seats</li>
                          <li>• 4K Digital Projection</li>
                          <li>• Climate Controlled</li>
                        </ul>
                      </div>
                      <div className="mb-2">
                        <strong>👤 Manager:</strong> {theatre.managerName}
                      </div>
                      <div className="mb-2">
                        <strong>📞 Contact:</strong> {theatre.managerContact}
                      </div>
                    </Card.Text>
                    <div className="mt-auto">
                      <Button
                        variant="primary"
                        onClick={() => navigate(`/shows?theatreId=${theatre.theatreId}`)}
                        className="w-100"
                      >
                        🎫 View Shows
                      </Button>
                    </div>
                  </Card.Body>
                </Card>
              </Col>
            ))}
          </Row>
        </>
      )}
    </Container>
  );
}

export default Theatres;
