import React, { useState, useEffect } from 'react';
import { Row, Col, Card, Button, Alert, Container } from 'react-bootstrap';
import axios from 'axios';

function Movies() {
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Movie poster images mapping for different genres
  const getMoviePoster = (movieName, genre) => {
    const moviePosters = {
      'Action': [
        'https://images.unsplash.com/photo-1536440136628-849c177e76a1?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
        'https://images.unsplash.com/photo-1594736797933-d0401ba2fe65?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
        'https://images.unsplash.com/photo-1581833971358-2c8b550f87b3?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'
      ],
      'Drama': [
        'https://images.unsplash.com/photo-1489599856772-16c0924af999?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
        'https://images.unsplash.com/photo-1485846234645-a62644f84728?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
        'https://images.unsplash.com/photo-1524985069026-dd778a71c7b4?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'
      ],
      'Comedy': [
        'https://images.unsplash.com/photo-1478720568477-b0ac077fe8e8?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
        'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
        'https://images.unsplash.com/photo-1440404653325-ab127d49abc1?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'
      ],
      'Horror': [
        'https://images.unsplash.com/photo-1520637836862-4d197d17c93a?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
        'https://images.unsplash.com/photo-1509248961158-d3f4ac1e18dc?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'
      ],
      'Romance': [
        'https://images.unsplash.com/photo-1518676590629-3dcbd9c5a5c9?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
        'https://images.unsplash.com/photo-1522075469751-3847cee04e8b?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'
      ],
      'Sci-Fi': [
        'https://images.unsplash.com/photo-1446776877081-d282a0f896e2?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
        'https://images.unsplash.com/photo-1581833971358-2c8b550f87b3?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'
      ]
    };

    const genrePosters = moviePosters[genre] || moviePosters['Action'];
    const hash = movieName.split('').reduce((a, b) => a + b.charCodeAt(0), 0);
    return genrePosters[hash % genrePosters.length];
  };

  // Generate random rating
  const generateRating = (movieName) => {
    const hash = movieName.split('').reduce((a, b) => a + b.charCodeAt(0), 0);
    const rating = 3 + (hash % 3); // Rating between 3-5
    return '★'.repeat(rating) + '☆'.repeat(5 - rating);
  };

  useEffect(() => {
    fetchMovies();
  }, []);

  const fetchMovies = async () => {
    try {
      setLoading(true);
      setError('');
      const response = await axios.get('/api/movies', {
        timeout: 10000,
        headers: {
          'Content-Type': 'application/json',
        }
      });
      setMovies(response.data);
    } catch (err) {
      console.error('Error fetching movies:', err);
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
      <div style={{ 
        minHeight: '100vh',
        background: 'linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center'
      }}>
        <Container className="text-center">
          <div className="loading-container animate-fade-in">
            <div 
              className="spinner-modern mb-4"
              style={{
                width: '4rem',
                height: '4rem',
                border: '4px solid rgba(102, 126, 234, 0.1)',
                borderTop: '4px solid #667eea',
                borderRadius: '50%',
                animation: 'spin 1s linear infinite',
                margin: '0 auto'
              }}
            ></div>
            <h4 className="mb-3" style={{ color: '#2c3e50', fontWeight: '600' }}>
              🎬 Loading Movies...
            </h4>
            <p className="text-muted mb-0">
              Fetching the latest blockbusters for you
            </p>
            <div className="mt-4">
              <div 
                className="progress"
                style={{ 
                  height: '6px', 
                  borderRadius: '10px',
                  background: 'rgba(102, 126, 234, 0.1)',
                  overflow: 'hidden'
                }}
              >
                <div 
                  className="progress-bar"
                  style={{
                    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                    animation: 'progress-animation 2s ease-in-out infinite'
                  }}
                ></div>
              </div>
            </div>
          </div>
        </Container>
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ 
        minHeight: '100vh',
        background: 'linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)',
        display: 'flex',
        alignItems: 'center',
        paddingTop: '80px'
      }}>
        <Container className="py-5">
          <Row className="justify-content-center">
            <Col md={8} lg={6}>
              <Card 
                className="modern-card text-center border-0 animate-fade-in"
                style={{
                  borderRadius: '25px',
                  background: 'rgba(255, 255, 255, 0.95)',
                  backdropFilter: 'blur(20px)',
                  boxShadow: '0 20px 60px rgba(0, 0, 0, 0.1)'
                }}
              >
                <Card.Body className="p-5">
                  <div className="mb-4">
                    <span 
                      style={{ 
                        fontSize: '4rem',
                        display: 'block',
                        marginBottom: '1rem'
                      }}
                    >
                      🎭
                    </span>
                    <h3 className="text-danger mb-3 fw-bold">
                      Oops! Something went wrong
                    </h3>
                  </div>
                  
                  <Alert 
                    variant="danger" 
                    className="mb-4 border-0"
                    style={{
                      background: 'linear-gradient(135deg, #ff6b6b, #ff8e8e)',
                      color: 'white',
                      borderRadius: '15px'
                    }}
                  >
                    <div className="d-flex align-items-center justify-content-center">
                      <span className="me-2">⚠️</span>
                      <div>
                        <strong>Connection Error</strong>
                        <div className="mt-1 small">{error}</div>
                      </div>
                    </div>
                  </Alert>

                  <div className="mb-4">
                    <p className="text-muted mb-3">
                      Don't worry! Here are a few things you can try:
                    </p>
                    <div className="text-start">
                      <div className="mb-2">
                        <span className="me-2">🔄</span>
                        <strong>Refresh the page</strong> - Sometimes a simple refresh helps
                      </div>
                      <div className="mb-2">
                        <span className="me-2">🖥️</span>
                        <strong>Check your connection</strong> - Make sure you're connected to the internet
                      </div>
                      <div className="mb-2">
                        <span className="me-2">⚙️</span>
                        <strong>Backend service</strong> - Ensure the server is running on port 8080
                      </div>
                    </div>
                  </div>

                  <Button
                    onClick={fetchMovies}
                    className="btn-modern"
                    style={{
                      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                      border: 'none',
                      borderRadius: '12px',
                      padding: '0.75rem 2rem',
                      fontWeight: '600',
                      color: 'white'
                    }}
                  >
                    <span className="me-2">🔄</span>
                    Try Again
                  </Button>
                </Card.Body>
              </Card>
            </Col>
          </Row>
        </Container>
      </div>
    );
  }

  return (
    <div style={{ 
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)',
      paddingTop: '90px',
      paddingBottom: '2rem'
    }}>
      <Container className="py-4">
        {/* Header Section */}
        <div className="text-center mb-5 animate-fade-in">
          <div className="mb-3">
            <span style={{ fontSize: '4rem', display: 'block' }}>🎭</span>
          </div>
          <h1 className="mb-3" style={{ 
            background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent',
            fontWeight: 'bold',
            fontSize: '3rem'
          }}>
            Movie Collection
          </h1>
          <p className="text-muted lead mb-4">
            Discover amazing movies and book your perfect entertainment experience
          </p>
          
          {/* Stats Section */}
          <div className="d-flex justify-content-center gap-3 mb-4">
            <div 
              className="glass-card px-4 py-2"
              style={{
                background: 'rgba(255, 255, 255, 0.1)',
                backdropFilter: 'blur(10px)',
                borderRadius: '25px',
                border: '1px solid rgba(255, 255, 255, 0.2)'
              }}
            >
              <div className="d-flex align-items-center">
                <span className="me-2">🎬</span>
                <strong style={{ color: '#667eea' }}>{movies.length}</strong>
                <span className="ms-1 text-muted">Movies</span>
              </div>
            </div>
            <div 
              className="glass-card px-4 py-2"
              style={{
                background: 'rgba(255, 255, 255, 0.1)',
                backdropFilter: 'blur(10px)',
                borderRadius: '25px',
                border: '1px solid rgba(255, 255, 255, 0.2)'
              }}
            >
              <div className="d-flex align-items-center">
                <span className="me-2">🌟</span>
                <strong style={{ color: '#667eea' }}>HD</strong>
                <span className="ms-1 text-muted">Quality</span>
              </div>
            </div>
          </div>
        </div>

        {movies.length === 0 ? (
          <div className="text-center py-5">
            <div className="mb-4">
              <span style={{ fontSize: '5rem', display: 'block', opacity: 0.5 }}>🎭</span>
            </div>
            <h4 className="mb-3 text-muted">No Movies Available</h4>
            <p className="text-muted">Check back later for new releases!</p>
            <Button
              onClick={fetchMovies}
              variant="outline-primary"
              className="mt-3"
              style={{
                borderRadius: '25px',
                padding: '0.75rem 2rem',
                fontWeight: '600'
              }}
            >
              <span className="me-2">🔄</span>
              Refresh Movies
            </Button>
          </div>
        ) : (
          <Row className="g-4">
            {movies.map((movie, index) => (
              <Col key={movie.movieId} lg={4} md={6} className="mb-4">
                <Card 
                  className="content-card h-100 border-0"
                  style={{
                    background: 'rgba(255, 255, 255, 0.95)',
                    backdropFilter: 'blur(10px)',
                    borderRadius: '20px',
                    overflow: 'hidden',
                    transition: 'all 0.3s ease',
                    animationDelay: `${index * 0.1}s`
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.transform = 'translateY(-10px) scale(1.02)';
                    e.currentTarget.style.boxShadow = '0 20px 40px rgba(0, 0, 0, 0.15)';
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.transform = 'translateY(0) scale(1)';
                    e.currentTarget.style.boxShadow = '0 4px 20px rgba(0, 0, 0, 0.08)';
                  }}
                >
                  <div style={{ position: 'relative', overflow: 'hidden' }}>
                    <Card.Img
                      variant="top"
                      src={getMoviePoster(movie.movieName, movie.movieGenre)}
                      style={{
                        height: '280px',
                        objectFit: 'cover',
                        transition: 'transform 0.5s ease'
                      }}
                      alt={movie.movieName}
                      onError={(e) => {
                        e.target.src = 'https://images.unsplash.com/photo-1489599856772-16c0924af999?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80';
                      }}
                    />
                    {/* Genre Badge */}
                    <div 
                      style={{
                        position: 'absolute',
                        top: '15px',
                        right: '15px',
                        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                        color: 'white',
                        padding: '0.4rem 0.8rem',
                        borderRadius: '20px',
                        fontSize: '0.8rem',
                        fontWeight: '600',
                        backdropFilter: 'blur(10px)'
                      }}
                    >
                      {movie.movieGenre}
                    </div>
                    {/* Rating */}
                    <div 
                      style={{
                        position: 'absolute',
                        bottom: '15px',
                        left: '15px',
                        background: 'rgba(0, 0, 0, 0.7)',
                        color: '#ffd700',
                        padding: '0.4rem 0.8rem',
                        borderRadius: '15px',
                        fontSize: '0.9rem',
                        fontWeight: '600',
                        backdropFilter: 'blur(10px)'
                      }}
                    >
                      {generateRating(movie.movieName)}
                    </div>
                  </div>
                  
                  <Card.Body className="p-4">
                    <div className="mb-3">
                      <h5 className="mb-2 fw-bold" style={{ color: '#2c3e50' }}>
                        {movie.movieName}
                      </h5>
                      <p className="text-muted small mb-2">
                        <span className="me-3">
                          <span className="me-1">🎬</span>
                          {movie.movieGenre}
                        </span>
                        <span className="me-3">
                          <span className="me-1">⏱️</span>
                          {movie.movieHours}
                        </span>
                        <span>
                          <span className="me-1">🗣️</span>
                          {movie.language}
                        </span>
                      </p>
                    </div>
                    
                    {movie.description && (
                      <p className="text-muted small mb-3" style={{ 
                        display: '-webkit-box',
                        WebkitLineClamp: 3,
                        WebkitBoxOrient: 'vertical',
                        overflow: 'hidden'
                      }}>
                        {movie.description}
                      </p>
                    )}
                    
                    <div className="mt-auto">
                      <Button
                        variant="primary"
                        href={`/shows?movieId=${movie.movieId}`}
                        className="w-100"
                        style={{
                          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                          border: 'none',
                          borderRadius: '12px',
                          padding: '0.75rem',
                          fontWeight: '600',
                          transition: 'all 0.3s ease'
                        }}
                        onMouseEnter={(e) => {
                          e.target.style.transform = 'translateY(-2px)';
                          e.target.style.boxShadow = '0 8px 25px rgba(102, 126, 234, 0.3)';
                        }}
                        onMouseLeave={(e) => {
                          e.target.style.transform = 'translateY(0)';
                          e.target.style.boxShadow = 'none';
                        }}
                      >
                        <span className="me-2">🎫</span>
                        View Shows & Book
                      </Button>
                    </div>
                  </Card.Body>
                </Card>
              </Col>
            ))}
          </Row>
        )}
      </Container>
    </div>
  );
}

export default Movies;
