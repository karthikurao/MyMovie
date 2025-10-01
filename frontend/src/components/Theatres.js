import React, { useState, useEffect } from 'react';
import { Row, Col, Card, Alert, Spinner, Container, Badge, Button } from 'react-bootstrap';
import axios from 'axios';

function Theatres() {
  const [theatres, setTheatres] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Theatre images for different cities/types
  const getTheatreImage = (theatreName, city) => {
    const theatreImages = [
      'https://images.unsplash.com/photo-1489599856772-16c0924af999?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
      'https://images.unsplash.com/photo-1596727147705-61a532a659bd?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
      'https://images.unsplash.com/photo-1536440136628-849c177e76a1?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
      'https://images.unsplash.com/photo-1594736797933-d0401ba2fe65?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
      'https://images.unsplash.com/photo-1440404653325-ab127d49abc1?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
      'https://images.unsplash.com/photo-1485846234645-a62644f84728?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'
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
      <div className="text-center mb-5">
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
                <Card className="theatre-card h-100">
                  <Card.Img
                    variant="top"
                    src={getTheatreImage(theatre.theatreName, theatre.theatreCity)}
                    style={{ height: '250px', objectFit: 'cover' }}
                    alt={theatre.theatreName}
                    onError={(e) => {
                      e.target.src = 'https://images.unsplash.com/photo-1489599856772-16c0924af999?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80';
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
                        href={`/shows?theatreId=${theatre.theatreId}`}
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
