import React from 'react';
import { Container, Row, Col } from 'react-bootstrap';

const journeySteps = [
    {
        step: '01',
        title: 'Choose Your Universe',
        description:
            'Browse curated collections, festival spotlights, and AI-personalized picks tuned to your vibe.',
        icon: '🪐',
    },
    {
        step: '02',
        title: 'Claim the Perfect Seat',
        description:
            'Heat-map seats based on previous reactions, comfort preferences, and friend availability.',
        icon: '🪑',
    },
    {
        step: '03',
        title: 'Inspire the Snack Alchemy',
        description:
            'Pair your movie with chef-designed menus or sustainable bites delivered fresh to your pod.',
        icon: '🍿',
    },
    {
        step: '04',
        title: 'Collect the Moments',
        description:
            'Earn digital memorabilia, behind-the-scenes drops, and shareable highlight reels after the show.',
        icon: '💫',
    },
];

function BookingJourney() {
    return (
        <section className="booking-journey py-5" aria-labelledby="journey-heading">
            <Container>
                <div className="section-heading text-center mb-5">
                    <span className="journey-subtitle">From Browse to Bravo</span>
                    <h2 id="journey-heading" className="display-5 fw-bold mb-3">
                        The CinemaHub Ritual
                    </h2>
                    <p className="lead mx-auto" style={{ maxWidth: '720px' }}>
                        Whether it is premiere night or a spontaneous weekday escape, we architect each step to
                        feel effortless, elevated, and a little bit magical.
                    </p>
                </div>

                <Row className="gy-4 gx-lg-5 justify-content-center">
                    {journeySteps.map((stepItem) => (
                        <Col key={stepItem.step} md={6} lg={3}>
                            <div className="journey-card h-100 text-start">
                                <div className="journey-icon" aria-hidden="true">
                                    {stepItem.icon}
                                </div>
                                <div className="journey-step">{stepItem.step}</div>
                                <h3 className="journey-title">{stepItem.title}</h3>
                                <p className="journey-description">{stepItem.description}</p>
                            </div>
                        </Col>
                    ))}
                </Row>
            </Container>
        </section>
    );
}

export default BookingJourney;
