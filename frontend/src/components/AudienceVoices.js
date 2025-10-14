import React from 'react';
import { Container, Carousel, Card, Badge } from 'react-bootstrap';

const voiceEntries = [
    {
        quote:
            '“CinemaHub turned our premiere night into an immersive journey. The synced lighting and concierge snacks had our audience buzzing before the opening credits.”',
        name: 'Aria Desai',
        role: 'Festival Curator, Prism Indie Week',
        avatarColor: 'linear-gradient(135deg, #667eea, #764ba2)',
        rating: 5,
    },
    {
        quote:
            '“The app remembered my comfort preferences and grouped our friends automatically. Booking is now a 30-second ritual instead of a spreadsheet.”',
        name: 'Malik Johnson',
        role: 'Community Host, Movie Mondays ATL',
        avatarColor: 'linear-gradient(135deg, #4facfe, #00f2fe)',
        rating: 4,
    },
    {
        quote:
            '“As a parent, I love the eco seats and plastic-free concessions. My kids are equally obsessed with the AR treasure hunts tucked around the lobby.”',
        name: 'Priya Nair',
        role: 'Founder, Green Families Club',
        avatarColor: 'linear-gradient(135deg, #43e97b, #38f9d7)',
        rating: 5,
    },
];

const renderStars = (count) =>
    Array.from({ length: count }).map((_, index) => (
        <span key={index} role="img" aria-label="star" className="voice-star">
            ⭐
        </span>
    ));

function AudienceVoices() {
    return (
        <section className="audience-voices py-5" aria-labelledby="audience-heading">
            <Container>
                <div className="section-heading text-center mb-5">
                    <Badge bg="light" text="dark" pill className="section-badge">
                        Voices of the Crowd
                    </Badge>
                    <h2 id="audience-heading" className="display-5 fw-bold mb-3">
                        Loved by Creators, Curators & Superfans
                    </h2>
                    <p className="lead mx-auto" style={{ maxWidth: '680px' }}>
                        We asked the people building communities around cinema to share what keeps them coming
                        back. Their stories help us craft the next wave of theatre magic.
                    </p>
                </div>

                <Carousel indicators={false} className="voices-carousel">
                    {voiceEntries.map((entry) => (
                        <Carousel.Item key={entry.name}>
                            <Card className="voice-card mx-auto">
                                <Card.Body>
                                    <div
                                        className="voice-avatar"
                                        style={{ background: entry.avatarColor }}
                                        aria-hidden="true"
                                    >
                                        {entry.name
                                            .split(' ')
                                            .map((segment) => segment.charAt(0))
                                            .join('')}
                                    </div>
                                    <blockquote className="voice-quote">{entry.quote}</blockquote>
                                    <div className="voice-rating" aria-label={`${entry.rating} out of 5 stars`}>
                                        {renderStars(entry.rating)}
                                    </div>
                                    <footer className="voice-meta">
                                        <div className="voice-name">{entry.name}</div>
                                        <div className="voice-role">{entry.role}</div>
                                    </footer>
                                </Card.Body>
                            </Card>
                        </Carousel.Item>
                    ))}
                </Carousel>
            </Container>
        </section>
    );
}

export default AudienceVoices;
